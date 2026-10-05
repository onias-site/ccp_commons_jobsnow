package com.ccp.aop;

/**
 * Ensures that every class of the dependent projects has a {@code toString()} implementation.
 * <p>
 * The aspect marks the target classes with the {@code CcpToString} interface and introduces into it, by ITD
 * (inter-type declaration), a {@code toString()} implementation that returns the JSON representing the object. When
 * the class has no instance attribute, the result is the class name instead of the JSON.
 * <p>
 * Why ITD and not an advice: AspectJ applies the ITD <b>only</b> to classes that do not have a {@code toString()} yet
 * (declared in them or inherited from a superclass). No existing implementation is overwritten; the compiler itself
 * performs that check. Besides, being a real method of the class, the generated toString also works for string
 * concatenation ({@code "" + obj}) and for calls coming from third-party libraries (logging, debugger), which an
 * {@code around} advice over {@code call(.. toString())} would not reach.
 * <p>
 * About the exclusions: when a marked type already inherits {@code toString()} from a class that is <b>not</b> being
 * woven (a binary type, such as the JDK ones), AspectJ cannot simply ignore the ITD and reports a compile error
 * ("inter-type declaration conflicts with existing member"). That is why the three families below must stay out of
 * the {@code declare parents}:
 * <ul>
 * <li>{@code com.ccp.aop..*}: the aspect infrastructure itself, as in the other aspects of this package.</li>
 * <li>{@code java.lang.Enum+} and {@code java.lang.Throwable+}: enums and exceptions already inherit
 * {@code toString()} from the JDK. Enums return the constant name and exceptions return type and message, which
 * is what is expected from them.</li>
 * <li>{@code hasmethod(*.new(..))}: restricts the marking to <b>classes</b> by requiring the type to have a
 * constructor. Interfaces have none, so they are left out. This is not a detail: marking an interface
 * propagates the marking to <i>everything</i> that implements it, enums included, which would conflict with
 * {@code Enum.toString()} again despite being excluded here. And the propagation is transitive: marking
 * {@code CcpService} alone would reach {@code JnService} and every service enum. Excluding interface by
 * interface would be endless; requiring a constructor solves the whole family at once.</li>
 * </ul>
 * {@code hasmethod} is an experimental AspectJ extension and requires the {@code XhasMember} parameter in the
 * aspectj-maven-plugin, already configured in the pom of every module. Without it the compilation fails with an
 * explicit message ("the type pattern hasmethod(..) can only be used when the -XhasMember option is set"), so removing
 * the parameter does not go unnoticed.
 * <p>
 * Being experimental, it has two limitations that have already cost time: two {@code has*} clauses in the same type
 * pattern make the pattern match <b>zero</b> types, silently (the same happens when chaining two
 * {@code declare parents}), so there is only one here; and the constructor syntax is {@code hasmethod(*.new(..))}:
 * written as {@code hasmethod(new(..))} it compiles normally and matches nothing. Since both cases fail silently,
 * when changing this pointcut check the marking by counting {@code @CcpGeneratedToString} in the .class files, not just
 * by whether the build passed.
 * <p>
 * A class of these packages that extends a third-party type with its own {@code toString()} (such as
 * {@code java.util.Date}) also conflicts, and must be excluded by its superclass, in the same format as
 * {@code java.lang.Throwable+}.
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

	/**
	 * Generated {@code toString()}: the JSON of the object's instance attributes, or the class name when there are none.
	 * @return the textual representation built by {@link CcpToStringBuilder#build(Object)}
	 */
	@CcpGeneratedToString
	public String CcpToString.toString() {
		return CcpToStringBuilder.build(this);
	}
}
