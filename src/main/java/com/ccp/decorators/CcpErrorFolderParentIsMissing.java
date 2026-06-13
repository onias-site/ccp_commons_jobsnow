package com.ccp.decorators;

import java.io.File;

/**
 * Exceção lançada quando se tenta ler ou criar um arquivo cujo diretório pai não existe no sistema de arquivos.
 */
@SuppressWarnings("serial")
public class CcpErrorFolderParentIsMissing extends RuntimeException {

	/**
	 * Monta a mensagem de erro incluindo o caminho absoluto do diretório pai ausente e o caminho completo do arquivo esperado.
	 * @param decorator o decorator do arquivo cujo diretório pai está ausente
	 */
	public CcpErrorFolderParentIsMissing(CcpFileDecorator decorator) {
		super("in the file " + new File(decorator.content).getParentFile().getAbsolutePath() + " is missing the file: " + decorator.content);

	}
	
}
