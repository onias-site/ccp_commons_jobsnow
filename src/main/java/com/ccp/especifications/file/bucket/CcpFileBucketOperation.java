package com.ccp.especifications.file.bucket;

import com.ccp.dependency.injection.CcpDependencyInjection;

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
	
	public final CcpFileBucketOperation execute(String tenant, String folderName, String... files) {
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
		for (String file : files) {
			this.execute(bucket, tenant, folderName, file);
		}
		return this;
	}
	public final String execute(String tenant, String folderName, String file) {
	
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);
			
		String execute = this.execute(bucket, tenant, folderName, file);
		
		return execute;
	}
}
