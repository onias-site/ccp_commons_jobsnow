package com.ccp.aop;

/**
 * Garante que toda classe dos projetos dependentes tenha {@code toString()} implementado.
 *
 * O aspecto marca as classes alvo com a interface {@code CcpToString} e introduz nela,
 * por ITD (inter-type declaration), uma implementação de {@code toString()} que devolve o
 * JSON representando o objeto. Quando a classe não tem nenhum atributo de instância, o
 * retorno é o nome da classe em vez do JSON.
 *
 * Por que ITD e não advice: o ITD é aplicado pelo AspectJ <b>somente</b> às classes que
 * ainda não possuem {@code toString()} (declarado nelas ou herdado de uma superclasse).
 * Nenhuma implementação existente é sobrescrita — o próprio compilador faz essa verificação,
 * que é exatamente o "verificar se a classe já tem toString" pedido. Além disso, por ser um
 * método real na classe, o toString gerado também vale para concatenação de strings
 * ({@code "" + obj}) e para chamadas vindas de bibliotecas de terceiros (log, depurador),
 * o que um {@code around} sobre {@code call(.. toString())} não alcançaria.
 *
 * Sobre as exclusões: quando um tipo marcado já herda {@code toString()} de uma classe que
 * <b>não</b> está sendo tecida (um tipo binário, como os do JDK), o AspectJ não consegue
 * simplesmente ignorar o ITD e acusa erro de compilação
 * ("inter-type declaration conflicts with existing member"). Por isso as três famílias
 * abaixo precisam ficar fora do {@code declare parents}:
 *
 * <ul>
 *   <li>{@code com.ccp.aop..*} — a própria infraestrutura do aspecto, como nos demais
 *       aspectos deste pacote.</li>
 *   <li>{@code java.lang.Enum+} e {@code java.lang.Throwable+} — enums e exceções já herdam
 *       {@code toString()} do JDK. Enums devolvem o nome da constante e exceções devolvem
 *       tipo e mensagem, que é o que se espera delas.</li>
 *   <li>{@code hasmethod(*.new(..))} — restringe a marcação a <b>classes</b>, exigindo que o
 *       tipo tenha construtor. Interfaces não têm, e ficam de fora. Isso não é um detalhe:
 *       marcar uma interface propaga a marcação para <i>tudo</i> que a implementa, inclusive
 *       enums, que voltariam a conflitar com {@code Enum.toString()} mesmo estando excluídos
 *       aqui. E a propagação é transitiva — bastaria marcar {@code CcpService} para atingir
 *       {@code JnService} e todos os enums de serviço. Excluir interface por interface seria
 *       interminável; exigir construtor resolve a família inteira de uma vez.</li>
 * </ul>
 *
 * {@code hasmethod} é uma extensão experimental do AspectJ e exige o parâmetro
 * {@code XhasMember} no aspectj-maven-plugin — já configurado no pom de todos os módulos.
 * Sem ele a compilação falha com mensagem explícita ("the type pattern hasmethod(..) can only
 * be used when the -XhasMember option is set"), então remover o parâmetro não passa
 * despercebido.
 *
 * Por ser experimental, tem duas limitações que já custaram tempo: duas cláusulas
 * {@code has*} no mesmo type pattern fazem o padrão casar <b>zero</b> tipos, em silêncio (e o
 * mesmo vale ao encadear em dois {@code declare parents}), por isso aqui só há uma; e a
 * sintaxe de construtor é {@code hasmethod(*.new(..))} — escrita como {@code hasmethod(new(..))}
 * ela compila normalmente e não casa nada. Como esses dois casos falham calados, ao mexer
 * neste pointcut confira a marcação contando {@code @CcpGeneratedToString} nos .class, e não
 * apenas se o build passou.
 *
 * Uma classe destes pacotes que estenda um tipo de terceiros com {@code toString()} próprio
 * (como {@code java.util.Date}) também conflita, e precisa ser excluída pela superclasse,
 * no mesmo formato de {@code java.lang.Throwable+}.
 */
public aspect CcpToStringAspect {

	declare parents:
		(
			(com.ccp..* || com.jn..* || com.jb..* || com.vis..*)
			&& !com.ccp.aop..*
			&& hasmethod(*.new(..))
			&& !java.lang.Enum+
			&& !java.lang.Throwable+
		) implements CcpToString;

	@CcpGeneratedToString
	public String CcpToString.toString() {
		return CcpToStringBuilder.build(this);
	}
}
