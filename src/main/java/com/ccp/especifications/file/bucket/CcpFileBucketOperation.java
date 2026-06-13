package com.ccp.especifications.file.bucket;

import com.ccp.dependency.injection.CcpDependencyInjection;

/**
 * Enum de operações de bucket que obtém a implementação via DI e aplica a operação
 * (deleção de pasta ou leitura) sobre um ou vários arquivos.
 */
public enum CcpFileBucketOperation {

	deleteFolder {
		String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName) {
			String result = bucket.delete(tenant, folderName);
			return result;
		}
	},
	get {
		String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName) {
			String result = bucket.get(tenant, folderName, fileName);
			return result;
		}
	},
	;
	abstract String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName);
	
	/**
	 * Aplica a operação sobre múltiplos arquivos e retorna o enum para encadeamento.
	 * @param tenant identificador do tenant
	 * @param folderName nome da pasta
	 * @param files arquivos a processar
	 * @return this para encadeamento
	 */
	public final CcpFileBucketOperation execute(String tenant, String folderName, String... files) {
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		for (String file : files) {
			this.execute(bucket, tenant, folderName, file);
		}
		return this;
	}
	/**
	 * Aplica a operação sobre um único arquivo e retorna o resultado.
	 * @param tenant identificador do tenant
	 * @param folderName nome da pasta
	 * @param file arquivo a processar
	 * @return resultado da operação
	 */
	public final String execute(String tenant, String folderName, String file) {
	
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
			
		String execute = this.execute(bucket, tenant, folderName, file);
		
		return execute;
	}
}
