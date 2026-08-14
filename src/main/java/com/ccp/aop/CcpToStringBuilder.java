package com.ccp.aop;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Constrói a representação JSON usada pelo {@code toString()} que o
 * {@code CcpToStringAspect} introduz nas classes que não implementam o método.
 *
 * Regras:
 * <ul>
 *   <li>Se a classe (e suas superclasses) não declara nenhum atributo de instância,
 *       o retorno é o nome da classe, e não um JSON.</li>
 *   <li>Caso contrário, o retorno é o JSON com os atributos de instância do objeto.</li>
 * </ul>
 *
 * Este builder <b>nunca lança exceção</b>: {@code toString()} é chamado em logs, mensagens
 * de erro e depuradores, onde uma falha mascararia o problema original. Qualquer erro
 * inesperado degrada para o nome da classe com o hash de identidade.
 *
 * Referências circulares e grafos muito profundos são cortados para evitar
 * {@code StackOverflowError}.
 */
public final class CcpToStringBuilder {

	/**
	 * Profundidade máxima de aninhamento antes de resumir o objeto pelo nome da classe.
	 */
	private static final int MAX_DEPTH = 6;

	/**
	 * Objetos em serialização na thread atual, para detectar referências circulares.
	 * IdentityHashMap porque a comparação precisa ser por referência: chamar equals/hashCode
	 * de um objeto arbitrário durante o toString poderia recursar ou falhar.
	 */
	private static final ThreadLocal<IdentityHashMap<Object, Object>> IN_PROGRESS =
			ThreadLocal.withInitial(IdentityHashMap::new);

	/**
	 * Cache dos atributos de instância por classe: a varredura reflexiva da hierarquia
	 * é feita uma única vez por classe.
	 */
	private static final Map<Class<?>, List<Field>> FIELDS_CACHE = new ConcurrentHashMap<>();

	private CcpToStringBuilder() {
	}

	/**
	 * Ponto de entrada chamado pelo {@code toString()} introduzido pelo aspecto.
	 */
	public static String build(Object object) {
		if (object == null) {
			return "null";
		}
		try {
			List<Field> fields = getInstanceFields(object.getClass());
			if (fields.isEmpty()) {
				return object.getClass().getName();
			}
			return writeObject(object, fields, 0);
		} catch (Throwable t) {
			return identityOf(object);
		}
	}

	/**
	 * Atributos de instância declarados na classe e em suas superclasses (exceto Object).
	 * Estáticos e sintéticos ficam de fora: os primeiros são estado da classe, não do objeto,
	 * e os segundos são gerados pelo compilador ou por instrumentação (this$0, $jacocoData).
	 */
	private static List<Field> getInstanceFields(Class<?> clazz) {
		return FIELDS_CACHE.computeIfAbsent(clazz, key -> {
			List<Field> fields = new ArrayList<>();
			for (Class<?> current = key; current != null && current != Object.class; current = current.getSuperclass()) {
				for (Field field : current.getDeclaredFields()) {
					if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					try {
						field.setAccessible(true);
					} catch (RuntimeException e) {
						continue;
					}
					fields.add(field);
				}
			}
			return fields;
		});
	}

	private static String writeObject(Object object, List<Field> fields, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		if (inProgress.containsKey(object)) {
			return quote("<circular reference: " + object.getClass().getName() + ">");
		}
		inProgress.put(object, object);
		try {
			StringBuilder sb = new StringBuilder("{");
			boolean first = true;
			for (Field field : fields) {
				Object value;
				try {
					value = field.get(object);
				} catch (IllegalAccessException | RuntimeException e) {
					continue;
				}
				if (first == false) {
					sb.append(",");
				}
				first = false;
				sb.append(quote(field.getName())).append(":").append(writeValue(value, depth + 1));
			}
			return sb.append("}").toString();
		} finally {
			inProgress.remove(object);
			if (inProgress.isEmpty()) {
				IN_PROGRESS.remove();
			}
		}
	}

	private static String writeValue(Object value, int depth) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof Number) {
			return writeNumber((Number) value);
		}
		if (value instanceof CharSequence || value instanceof Character) {
			return quote(value.toString());
		}
		if (value instanceof Enum) {
			return quote(((Enum<?>) value).name());
		}
		if (depth > MAX_DEPTH) {
			return quote(value.getClass().getName());
		}
		if (value.getClass().isArray()) {
			return writeArray(value, depth);
		}
		if (value instanceof Collection) {
			return writeCollection((Collection<?>) value, depth);
		}
		if (value instanceof Map) {
			return writeMap((Map<?, ?>) value, depth);
		}
		// Se o tipo tem toString próprio, ele é a melhor representação disponível: usa o
		// texto dele. Cobre datas, UUID, BigDecimal e qualquer classe com toString customizado.
		if (hasOwnToString(value.getClass())) {
			return quote(String.valueOf(value));
		}
		List<Field> fields = getInstanceFields(value.getClass());
		if (fields.isEmpty()) {
			return quote(value.getClass().getName());
		}
		return writeObject(value, fields, depth);
	}

	/**
	 * Indica se o toString do tipo foi escrito à mão, e não gerado pelo aspecto.
	 *
	 * Vale a primeira declaração encontrada subindo a hierarquia, que é a que o objeto de fato
	 * executa. Se ela tem {@code @CcpGeneratedToString}, o toString é o deste builder: nesse
	 * caso o objeto é expandido como JSON aninhado, em vez de virar uma string com JSON
	 * escapado dentro.
	 */
	private static boolean hasOwnToString(Class<?> clazz) {
		for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
			try {
				return current.getDeclaredMethod("toString")
						.isAnnotationPresent(CcpGeneratedToString.class) == false;
			} catch (NoSuchMethodException e) {
				continue;
			} catch (RuntimeException e) {
				return true;
			}
		}
		return false;
	}

	private static String writeNumber(Number number) {
		double asDouble = number.doubleValue();
		// JSON não representa NaN nem infinito: esses valores viram texto.
		if (Double.isNaN(asDouble) || Double.isInfinite(asDouble)) {
			return quote(number.toString());
		}
		return number.toString();
	}

	private static String writeArray(Object array, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		if (inProgress.containsKey(array)) {
			return quote("<circular reference: " + array.getClass().getName() + ">");
		}
		inProgress.put(array, array);
		try {
			StringBuilder sb = new StringBuilder("[");
			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(writeValue(Array.get(array, i), depth + 1));
			}
			return sb.append("]").toString();
		} finally {
			inProgress.remove(array);
		}
	}

	private static String writeCollection(Collection<?> collection, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		if (inProgress.containsKey(collection)) {
			return quote("<circular reference: " + collection.getClass().getName() + ">");
		}
		inProgress.put(collection, collection);
		try {
			StringBuilder sb = new StringBuilder("[");
			boolean first = true;
			for (Object item : collection) {
				if (first == false) {
					sb.append(",");
				}
				first = false;
				sb.append(writeValue(item, depth + 1));
			}
			return sb.append("]").toString();
		} catch (RuntimeException e) {
			return quote(identityOf(collection));
		} finally {
			inProgress.remove(collection);
		}
	}

	private static String writeMap(Map<?, ?> map, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		if (inProgress.containsKey(map)) {
			return quote("<circular reference: " + map.getClass().getName() + ">");
		}
		inProgress.put(map, map);
		try {
			StringBuilder sb = new StringBuilder("{");
			boolean first = true;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (first == false) {
					sb.append(",");
				}
				first = false;
				sb.append(quote(String.valueOf(entry.getKey()))).append(":")
						.append(writeValue(entry.getValue(), depth + 1));
			}
			return sb.append("}").toString();
		} catch (RuntimeException e) {
			return quote(identityOf(map));
		} finally {
			inProgress.remove(map);
		}
	}

	private static String identityOf(Object object) {
		return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
	}

	private static String quote(String text) {
		StringBuilder sb = new StringBuilder(text.length() + 2).append('"');
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			default:
				if (c < 0x20) {
					sb.append(String.format("\\u%04x", (int) c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.append('"').toString();
	}
}
