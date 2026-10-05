package com.ccp.especifications.file.bucket;

import com.ccp.dependency.injection.CcpDependencyInjection;

/**
 * Bucket operations that take the {@code CcpFileBucket} implementation from the dependency injection and apply the
 * operation (folder deletion or reading) over one or several files.
 */
public enum CcpFileBucketOperation {

	/** Deletes the whole folder (the file name is ignored). */
	deleteFolder {
		/**
		 * Deletes the folder.
		 * @param bucket the bucket implementation
		 * @param tenant tenant identifier
		 * @param folderName folder name
		 * @param fileName ignored
		 * @return the result of the deletion
		 */
		String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName) {
			String result = bucket.delete(tenant, folderName);
			return result;
		}
	},
	/** Reads a file of the folder. */
	get {
		/**
		 * Reads the file.
		 * @param bucket the bucket implementation
		 * @param tenant tenant identifier
		 * @param folderName folder name
		 * @param fileName file name
		 * @return the file content
		 */
		String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName) {
			String result = bucket.get(tenant, folderName, fileName);
			return result;
		}
	},
	;
	/**
	 * Applies the operation through the given bucket implementation.
	 * @param bucket the bucket implementation
	 * @param tenant tenant identifier
	 * @param folderName folder name
	 * @param fileName file name
	 * @return the result of the operation
	 */
	abstract String execute(CcpFileBucket bucket, String tenant, String folderName, String fileName);
	
	/**
	 * Applies the operation to each file, discarding the results.
	 * @param tenant tenant identifier
	 * @param folderName folder name
	 * @param files the files
	 * @return this operation, for chaining
	 */
	public final CcpFileBucketOperation execute(String tenant, String folderName, String... files) {
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		for (String file : files) {
			this.execute(bucket, tenant, folderName, file);
		}
		return this;
	}
	/**
	 * Applies the operation to one file.
	 * @param tenant tenant identifier
	 * @param folderName folder name
	 * @param file the file
	 * @return the result of the operation
	 */
	public final String execute(String tenant, String folderName, String file) {
	
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
			
		String execute = this.execute(bucket, tenant, folderName, file);
		
		return execute;
	}
}
