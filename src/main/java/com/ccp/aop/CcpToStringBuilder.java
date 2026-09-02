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
import java.lang.reflect.Method;

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
		boolean objectIgual = object == null;
		if (objectIgual) {
			return "null";
		}
		try {
			var objectClass = object.getClass();
			List<Field> fields = getInstanceFields(objectClass);
			boolean fieldsEmpty = fields.isEmpty();
			if (fieldsEmpty) {
				var objectClass2 = object.getClass();
				var objectClass2Name = objectClass2.getName();
				return objectClass2Name;
			}
			String writeObject = writeObject(object, fields, 0);
			return writeObject;
		} catch (Throwable t) {
			String identityOf = identityOf(object);
			return identityOf;
		}
	}

	/**
	 * Atributos de instância declarados na classe e em suas superclasses (exceto Object).
	 * Estáticos e sintéticos ficam de fora: os primeiros são estado da classe, não do objeto,
	 * e os segundos são gerados pelo compilador ou por instrumentação (this$0, $jacocoData).
	 */
	private static List<Field> getInstanceFields(Class<?> clazz) {
		List<Field> computeIfAbsent = FIELDS_CACHE.computeIfAbsent(clazz, key -> {
			List<Field> fields = new ArrayList<>();
			for (Class<?> current = key; current != null && current != Object.class; current = current.getSuperclass()) {
				Field[] declaredFields = current.getDeclaredFields();
				for (Field field : declaredFields) {
					boolean synthetic = field.isSynthetic();
					boolean syntheticOu = synthetic || Modifier.isStatic(field.getModifiers());
					if (syntheticOu) {
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
		return computeIfAbsent;
	}

	private static String writeObject(Object object, List<Field> fields, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		boolean containsKey = inProgress.containsKey(object);
		if (containsKey) {
			var objectClass3 = object.getClass();
			var objectClass3Name = objectClass3.getName();
			String valorMais = "<circular reference: " + objectClass3Name;
			String valorMaisMais = valorMais + ">";
			String quote = quote(valorMaisMais);
			return quote;
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
				boolean firstIgual = first == false;
				if (firstIgual) {
					sb.append(",");
				}
				first = false;
				String fieldName = field.getName();
				String quote2 = quote(fieldName);
				StringBuilder append = sb.append(quote2);
				StringBuilder append2 = append.append(":");
				int depthMais = depth + 1;
				String writeValue = writeValue(value, depthMais);
				append2.append(writeValue);
			}
			StringBuilder append3 = sb.append("}");
			String toString = append3.toString();
			return toString;
		} finally {
			inProgress.remove(object);
			boolean inProgressEmpty = inProgress.isEmpty();
			if (inProgressEmpty) {
				IN_PROGRESS.remove();
			}
		}
	}

	private static String writeValue(Object value, int depth) {
		boolean valueIgual = value == null;
		if (valueIgual) {
			return "null";
		}
		boolean isBoolean = value instanceof Boolean;
		if (isBoolean) {
			String toString2 = value.toString();
			return toString2;
		}
		boolean isNumber = value instanceof Number;
		if (isNumber) {
			Number number2 = (Number) value;
			String writeNumber = writeNumber(number2);
			return writeNumber;
		}
		boolean isCharSequence = value instanceof CharSequence;
		boolean isCharSequenceOu = isCharSequence || value instanceof Character;
		if (isCharSequenceOu) {
			String toString3 = value.toString();
			String quote3 = quote(toString3);
			return quote3;
		}
		boolean isEnum = value instanceof Enum;
		if (isEnum) {
			Enum<?> valor = (Enum<?>) value;
			String valueName = (valor).name();
			String quote4 = quote(valueName);
			return quote4;
		}
		boolean depthMaior = depth > MAX_DEPTH;
		if (depthMaior) {
			var valueClass = value.getClass();
			String valueClassName = valueClass.getName();
			String quote5 = quote(valueClassName);
			return quote5;
		}
		var valueClass2 = value.getClass();
		var array2 = valueClass2.isArray();
		if (array2) {
			String writeArray = writeArray(value, depth);
			return writeArray;
		}
		boolean isCollection = value instanceof Collection;
		if (isCollection) {
			Collection<?> collection2 = (Collection<?>) value;
			String writeCollection = writeCollection(collection2, depth);
			return writeCollection;
		}
		boolean isMap = value instanceof Map;
		if (isMap) {
			Map<?, ?> map2 = (Map<?, ?>) value;
			String writeMap = writeMap(map2, depth);
			return writeMap;
		}
		var valueClass3 = value.getClass();
		boolean ownToString = hasOwnToString(valueClass3);
		// Se o tipo tem toString próprio, ele é a melhor representação disponível: usa o
		// texto dele. Cobre datas, UUID, BigDecimal e qualquer classe com toString customizado.
		if (ownToString) {
			String valueOf = String.valueOf(value);
			String quote6 = quote(valueOf);
			return quote6;
		}
		var valueClass4 = value.getClass();
		List<Field> fields = getInstanceFields(valueClass4);
		boolean fieldsEmpty2 = fields.isEmpty();
		if (fieldsEmpty2) {
			var valueClass5 = value.getClass();
			String valueClass5Name = valueClass5.getName();
			String quote7 = quote(valueClass5Name);
			return quote7;
		}
		String writeObject2 = writeObject(value, fields, depth);
		return writeObject2;
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
				Method declaredMethod = current.getDeclaredMethod("toString");
				var annotationPresent = declaredMethod
						.isAnnotationPresent(CcpGeneratedToString.class);
						boolean annotationPresentIgual = annotationPresent == false;
						return annotationPresentIgual;
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
		boolean naN = Double.isNaN(asDouble);
		boolean naNOu = naN || Double.isInfinite(asDouble);
		// JSON não representa NaN nem infinito: esses valores viram texto.
		if (naNOu) {
			String toString4 = number.toString();
			String quote8 = quote(toString4);
			return quote8;
		}
		String toString5 = number.toString();
		return toString5;
	}

	private static String writeArray(Object array, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		boolean containsKey2 = inProgress.containsKey(array);
		if (containsKey2) {
			var arrayClass = array.getClass();
			var arrayClassName = arrayClass.getName();
			String valorMais2 = "<circular reference: " + arrayClassName;
			String valorMais2Mais = valorMais2 + ">";
			String quote9 = quote(valorMais2Mais);
			return quote9;
		}
		inProgress.put(array, array);
		try {
			StringBuilder sb = new StringBuilder("[");
			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				boolean iMaior = i > 0;
				if (iMaior) {
					sb.append(",");
				}
				var get = Array.get(array, i);
				int depthMais2 = depth + 1;
				String writeValue2 = writeValue(get, depthMais2);
				sb.append(writeValue2);
			}
			StringBuilder append4 = sb.append("]");
			String toString6 = append4.toString();
			return toString6;
		} finally {
			inProgress.remove(array);
		}
	}

	private static String writeCollection(Collection<?> collection, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		boolean containsKey3 = inProgress.containsKey(collection);
		if (containsKey3) {
			var collectionClass = collection.getClass();
			var collectionClassName = collectionClass.getName();
			String valorMais3 = "<circular reference: " + collectionClassName;
			String valorMais3Mais = valorMais3 + ">";
			String quote10 = quote(valorMais3Mais);
			return quote10;
		}
		inProgress.put(collection, collection);
		try {
			StringBuilder sb = new StringBuilder("[");
			boolean first = true;
			for (Object item : collection) {
				boolean firstIgual2 = first == false;
				if (firstIgual2) {
					sb.append(",");
				}
				first = false;
				int depthMais3 = depth + 1;
				String writeValue3 = writeValue(item, depthMais3);
				sb.append(writeValue3);
			}
			StringBuilder append5 = sb.append("]");
			String toString7 = append5.toString();
			return toString7;
		} catch (RuntimeException e) {
			String identityOf2 = identityOf(collection);
			String quote11 = quote(identityOf2);
			return quote11;
		} finally {
			inProgress.remove(collection);
		}
	}

	private static String writeMap(Map<?, ?> map, int depth) {
		IdentityHashMap<Object, Object> inProgress = IN_PROGRESS.get();
		boolean containsKey4 = inProgress.containsKey(map);
		if (containsKey4) {
			var mapClass = map.getClass();
			var mapClassName = mapClass.getName();
			String valorMais4 = "<circular reference: " + mapClassName;
			String valorMais4Mais = valorMais4 + ">";
			String quote12 = quote(valorMais4Mais);
			return quote12;
		}
		inProgress.put(map, map);
		try {
			StringBuilder sb = new StringBuilder("{");
			boolean first = true;
			var entrySet = map.entrySet();
			for (Map.Entry<?, ?> entry : entrySet) {
				boolean firstIgual3 = first == false;
				if (firstIgual3) {
					sb.append(",");
				}
				first = false;
				var entryKey = entry.getKey();
				String valueOf2 = String.valueOf(entryKey);
				String quote13 = quote(valueOf2);
				StringBuilder append6 = sb.append(quote13);
				StringBuilder append7 = append6.append(":");
				var entryValue = entry.getValue();
				int depthMais4 = depth + 1;
				String writeValue4 = writeValue(entryValue, depthMais4);
				append7
						.append(writeValue4);
			}
			StringBuilder append8 = sb.append("}");
			String toString8 = append8.toString();
			return toString8;
		} catch (RuntimeException e) {
			String identityOf3 = identityOf(map);
			String quote14 = quote(identityOf3);
			return quote14;
		} finally {
			inProgress.remove(map);
		}
	}

	private static String identityOf(Object object) {
		var objectClass4 = object.getClass();
		var objectClass4Name = objectClass4.getName();
		var objectClass4NameMais = objectClass4Name + "@";
		int identityHashCode = System.identityHashCode(object);
		String toHexString = Integer.toHexString(identityHashCode);
		var objectClass4NameMaisMais = objectClass4NameMais + toHexString;
		return objectClass4NameMaisMais;
	}

	private static String quote(String text) {
		int textLength = text.length();
		int textLengthMais = textLength + 2;
		StringBuilder stringBuilder = new StringBuilder(textLengthMais);
		StringBuilder sb = stringBuilder.append('"');
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
					int valor2 = (int) c;
					String stringFormat = String.format("\\u%04x", valor2);
					sb.append(stringFormat);
				} else {
					sb.append(c);
				}
			}
		}
		StringBuilder append9 = sb.append('"');
		String toString9 = append9.toString();
		return toString9;
	}
}
