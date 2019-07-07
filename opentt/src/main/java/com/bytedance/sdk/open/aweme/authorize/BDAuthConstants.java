package com.bytedance.sdk.open.aweme.authorize;

/**
 * 请求参数
 * Created by yangzhirong on 2018/10/22.
 */
public interface BDAuthConstants {
    String QUERY_RESPONSE_TYPE = "response_type";
    String QUERY_REDIRECT_URI = "redirect_uri";
    String QUERY_CLIENT_KEY = "client_key";
    String QUERY_STATE = "state";
    String QUERY_FROM = "from";
    String QUERY_SCOPE = "scope";
    String QUERY_OPTIONAL_SCOPE = "optionalScope";
    String QUERY_SIGNATURE = "signature";
    String VALUE_FROM_OPENSDK = "opensdk";
    String VALUE_RESPONSE_TYPE_CODE = "code";
    String SCHEMA_HTTPS = "https";
    String REDIRECT_QUERY_CODE = "code";
    String REDIRECT_QUERY_STATE = "state";
    String REDIRECT_QUERY_ERROR_CODE = "errCode";
    String REDIRECT_QUERY_SCOPE = "scopes";
}
