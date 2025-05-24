package com.ccp.especifications.file.bucket;

public interface CcpFileBucket {

	String get(String tenant, String bucketName, String fileName);

	String delete(String tenant, String bucketName, String fileName);

	String delete(String tenant, String bucketName);
	
	String save(String tenant, String bucketName, String fileName, String fileContent);

	
}
