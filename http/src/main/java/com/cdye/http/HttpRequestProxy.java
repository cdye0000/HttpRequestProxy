package com.cdye.http;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.List;
import java.util.Map;



public class HttpRequestProxy implements IHttpRequest {
    private IHttpRequest httpRequest;

    public HttpRequestProxy(IHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    private static volatile HttpRequestProxy instance;
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
    public void cancel(Integer tag){
        httpRequest.cancel(tag);
    }
    public void cancelAll(){
        httpRequest.cancelAll();
    }

    @Override
    public void get(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading) {
        httpRequest.get(url,params,iCallBack,tag,showLoading);
    }
    public void get(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag){
        get(url,params,iCallBack,tag,false);
    }

    @Override
    public void post(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading) {
        httpRequest.post(url,params,iCallBack,tag,showLoading);
    }
    public void post(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag) {
        post(url,params,iCallBack,tag,false);
    }
    @Override
    public void postJson(String url, String jsonString, ICallBack iCallBack, Integer tag,boolean showLoading) {
        httpRequest.postJson(url,jsonString,iCallBack,tag,showLoading);
    }

    @Override
    public void put(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag, boolean showLoading) {

    }

    @Override
    public void putJson(String url, String jsonString, ICallBack iCallBack, Integer tag, boolean showLoading) {
        httpRequest.putJson(url,jsonString,iCallBack,tag,showLoading);
    }

    @Override
    public void delete(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag, boolean showLoading) {

    }

    public void postJson(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading){
        httpRequest.postJson(url, JSON.toJSONString(params),iCallBack,tag,showLoading);
    }
    public void postJson(String url, String jsonString, ICallBack iCallBack, Integer tag){
        postJson(url,jsonString,iCallBack,tag,false);
    }
    public void postJson(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag){
        postJson(url, JSON.toJSONString(params),iCallBack,tag,false);
    }
    @Override
    public void upload(String url, String paramName, File file, ICallBack iCallBack, Integer tag, boolean showLoading) {
        httpRequest.upload(url,paramName,file,iCallBack,tag,showLoading);
    }
    public void upload(String url, String paramName, File file, ICallBack iCallBack, Integer tag) {
        upload(url,paramName,file,iCallBack,tag,false);
    }

    @Override
    public void upload(String url, String paramName, List<File> files, ICallBack iCallBack, Integer tag, boolean showLoading) {
        httpRequest.upload(url,paramName,files,iCallBack,tag,showLoading);
    }

    public void upload(String url, String paramName, List<File> files, ICallBack iCallBack, Integer tag) {
        upload(url,paramName,files,iCallBack,tag,false);
    }
}

