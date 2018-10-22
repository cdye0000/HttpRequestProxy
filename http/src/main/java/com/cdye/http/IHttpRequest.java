package com.cdye.http;


import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest 接口定义get / post网络请求
 */

public interface IHttpRequest {
    void get(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading);

    void post(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading);

    void postJson(String url, String jsonString, ICallBack iCallBack, Integer tag,boolean showLoading);

    void put(String url,  Map<String, Object> params, ICallBack iCallBack, Integer tag, boolean showLoading);

    void putJson(String url, String jsonString, ICallBack iCallBack, Integer tag, boolean showLoading);



    void delete(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag,boolean showLoading);

    void upload(String url, String paramName, File file, ICallBack iCallBack, Integer tag, boolean showLoading);

    void upload(String url, String paramName, List<File> files, ICallBack iCallBack, Integer tag, boolean showLoading);



    void cancel(Integer tag);
    void cancelAll();
}

