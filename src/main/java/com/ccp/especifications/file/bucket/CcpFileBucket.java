package com.ccp.especifications.file.bucket;

/** Contract for file storage in buckets (GCP Storage): read, save and delete files or folders by tenant and name. */
public interface CcpFileBucket {

	/**
	 * Reads the content of a file of the bucket.
	 * @param tenant tenant identifier
	 * @param bucketName bucket name
	 * @param fileName file name
	 * @return the file content as text
	 */
	String get(String tenant, String bucketName, String fileName);

	/**
	 * Deletes one file of the bucket.
	 * @param tenant tenant identifier
	 * @param bucketName bucket name
	 * @param fileName file name
	 * @return the result of the operation
	 */
	String delete(String tenant, String bucketName, String fileName);

	/**
	 * Deletes a whole folder of the bucket.
	 * @param tenant tenant identifier
	 * @param bucketName bucket/folder name
	 * @return the result of the operation
	 */
	String delete(String tenant, String bucketName);

	/**
	 * Saves a file in the bucket.
	 * @param tenant tenant identifier
	 * @param bucketName bucket name
	 * @param fileName file name
	 * @param fileContent file content
	 * @return the result of the operation
	 */
	String save(String tenant, String bucketName, String fileName, String fileContent);

	
}
