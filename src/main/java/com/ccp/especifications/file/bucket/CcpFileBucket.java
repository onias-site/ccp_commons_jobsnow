package com.ccp.especifications.file.bucket;

/**
 * Contrato para operações de armazenamento de arquivos em bucket (GCP Storage):
 * recuperar, salvar e excluir arquivos ou pastas por tenant e nome.
 */
public interface CcpFileBucket {

	/**
	 * Recupera o conteúdo de um arquivo no bucket.
	 * @param tenant identificador do tenant
	 * @param bucketName nome do bucket
	 * @param fileName nome do arquivo
	 * @return conteúdo do arquivo como String
	 */
	String get(String tenant, String bucketName, String fileName);

	/**
	 * Remove um arquivo específico do bucket.
	 * @param tenant identificador do tenant
	 * @param bucketName nome do bucket
	 * @param fileName nome do arquivo
	 * @return resultado da operação
	 */
	String delete(String tenant, String bucketName, String fileName);

	/**
	 * Remove uma pasta inteira do bucket.
	 * @param tenant identificador do tenant
	 * @param bucketName nome do bucket/pasta
	 * @return resultado da operação
	 */
	String delete(String tenant, String bucketName);

	/**
	 * Salva um arquivo no bucket.
	 * @param tenant identificador do tenant
	 * @param bucketName nome do bucket
	 * @param fileName nome do arquivo
	 * @param fileContent conteúdo do arquivo
	 * @return resultado da operação
	 */
	String save(String tenant, String bucketName, String fileName, String fileContent);

	
}
