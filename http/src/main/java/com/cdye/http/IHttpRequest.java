package com.cdye.http;


import java.util.Map;

/**
 * HttpRequest 接口定义get / post网络请求
 */

public interface IHttpRequest {
    void get(String url, Map<String,String> params, ICallBack iCallBack);

    void post(String url, Map<String,String> params,ICallBack iCallBack);
}

