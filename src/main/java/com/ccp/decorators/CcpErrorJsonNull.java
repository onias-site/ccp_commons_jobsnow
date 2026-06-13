package com.ccp.decorators;

/**
 * Exceção lançada quando se tenta construir um {@code CcpJsonRepresentation} a partir de um {@code Map} nulo.
 * Sinaliza que o JSON passado é {@code null} e portanto inválido como entrada.
 */
@SuppressWarnings("serial")
public class CcpErrorJsonNull extends RuntimeException{

}
