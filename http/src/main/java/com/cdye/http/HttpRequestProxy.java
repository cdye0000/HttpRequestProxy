package com.cdye.http;

import java.util.Map;

/**
 * 网络请求代理
 * 使用之前需要init 初始化指定具体request（实际网络请求对象）
 */

public class HttpRequestProxy implements IHttpRequest {
    private IHttpRequest httpRequest;

    public HttpRequestProxy(IHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    private static HttpRequestProxy instance;
    //指定实际网络请求对象
    public static void init(IHttpRequest httpRequest){
        if (instance==null){
            synchronized (HttpRequestProxy.class){
                if (instance==null){
                    instance=new HttpRequestProxy(httpRequest);
                }
            }
        }
    }

    public static HttpRequestProxy getInstance() {
        return instance;
    }

    @Override
    public void get(String url, Map<String, String> params, ICallBack iCallBack) {
        httpRequest.get(url,params,iCallBack);
    }

    @Override
    public void post(String url, Map<String, String> params, ICallBack iCallBack) {
        httpRequest.post(url,params,iCallBack);
    }
}

