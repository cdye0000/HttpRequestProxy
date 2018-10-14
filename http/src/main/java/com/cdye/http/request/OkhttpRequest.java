package com.cdye.http.request;




import com.cdye.http.ICallBack;
import com.cdye.http.IHttpRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http请求具体实现
 * OkHttpClient 实现网络请求
 */

public class OkhttpRequest implements IHttpRequest {
    private OkHttpClient okHttpClient;

    public OkhttpRequest() {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public void get(String url, Map<String, String> params, final ICallBack iCallBack) {

        StringBuilder stringBuilder=new StringBuilder("?");
        if (params!=null){
            Set<String> keySet=params.keySet();
            for (String key : keySet) {
                stringBuilder.append(key).append("=").append(params.get(key)).append("&");
            }

        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        Request request=new Request.Builder().url(url+stringBuilder.toString()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                iCallBack.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null&&response.isSuccessful()){
                    iCallBack.onSuccess(response.body().string());
                }else {
                    iCallBack.onFailure(new Throwable(""));
                }
            }
        });
    }

    @Override
    public void post(String url, Map<String, String> params, final ICallBack iCallBack) {
        FormBody.Builder builder=new FormBody.Builder();
        if (params!=null){
            Set<String> keySet=params.keySet();
            for (String key : keySet) {
                builder.add(key,params.get(key));
            }
        }
        RequestBody requestBody= builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                iCallBack.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null&&response.isSuccessful()){
                    iCallBack.onSuccess(response.body().string());
                }else {
                    iCallBack.onFailure(new Throwable(""));
                }
            }
        });
    }
}

