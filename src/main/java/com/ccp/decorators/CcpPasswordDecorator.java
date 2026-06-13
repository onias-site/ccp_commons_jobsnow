package com.ccp.decorators;

/**
 * Decorator sobre uma string de senha que valida força. Encapsula a senha e oferece verificação se ela
 * atende aos critérios de complexidade do sistema (ao menos um dígito, uma letra minúscula, uma maiúscula,
 * um caractere especial e entre 8 e 20 caracteres).
 */
public class CcpPasswordDecorator implements CcpDecorator<String> {

	public final String content;

	/**
	 * Encapsula a senha.
	 */
	protected CcpPasswordDecorator(String content) {
		this.content = content;
	}

	/**
	 * Retorna a senha bruta.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Verifica se a senha atende à regex de complexidade do sistema.
	 */
	public boolean isStrong() {
		if (this.content.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")){
		   return true;
		} 
		return false;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna a senha.
	 */
	public String getContent() {
		return this.content;
	}

}
