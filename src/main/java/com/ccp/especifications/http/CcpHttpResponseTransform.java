package com.ccp.especifications.http;

public interface CcpHttpResponseTransform<V> {

	V transform(CcpHttpResponse response);
}
