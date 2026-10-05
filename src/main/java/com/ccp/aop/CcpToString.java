package com.ccp.aop;

/**
 * Marker interface applied by {@code CcpToStringAspect} (through {@code declare parents}) to every class of the
 * packages covered by the aspect.
 * <p>
 * The {@code toString()} implementation is introduced into this interface by ITD (inter-type declaration). AspectJ
 * only injects the method into classes that do NOT already have their own {@code toString()} (declared in them or
 * inherited from a superclass), so no existing implementation is overwritten.
 * <p>
 * Not meant to be implemented by hand.
 */
public interface CcpToString {

}
