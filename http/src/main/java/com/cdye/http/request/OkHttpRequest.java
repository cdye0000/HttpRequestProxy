package com.cdye.http.request;




import android.annotation.SuppressLint;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cdye.http.ICallBack;
import com.cdye.http.IHttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http请求具体实现
 * OkHttpClient 实现网络请求
 */

public class OkHttpRequest implements IHttpRequest {
    private OkHttpClient okHttpClient;
    private OkHttpRequest okhttpRequest;
    private List<Interceptor> interceptors;

    public OkHttpRequest() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        builder.sslSocketFactory(createSSLSocketFactory());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        okHttpClient = builder.build();

    }
    public OkHttpRequest(OkHttpClient okHttpClient){
        this.okHttpClient=okHttpClient;
    }


    public void cancelTag(Integer tag) {
        if (tag == null) return;
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    @Override
    public void get(final String url, final Map<String, Object> params, final ICallBack iCallBack, final Integer tag,boolean showLoading) {
        StringBuilder stringBuilder = new StringBuilder("?");
        if (params != null) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                stringBuilder.append(key).append("=").append(String.valueOf(params.get(key))).append("&");
            }

        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Request.Builder builder = new Request.Builder();
        final Request request = builder.tag(tag).url(url + stringBuilder.toString()).get().build();
        request(request, iCallBack,tag);
    }

    @Override
    public void post(final String url, final Map<String, Object> params, final ICallBack iCallBack, Integer tag,boolean showLoading) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                builder.add(key, String.valueOf(params.get(key)));
            }
        }
        RequestBody requestBody = builder.build();

        Request.Builder builder1 = new Request.Builder();
        final Request request = builder1
                .url(url)
                .post(requestBody)
                .build();
        request(request, iCallBack,tag);
    }

    @Override
    public void postJson(final String url, final String jsonString, final ICallBack iCallBack, final Integer tag,boolean showLoading) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonString);
        Request.Builder builder = new Request.Builder();
        final Request request = builder.tag(tag)
                .url(url)
                .post(requestBody)
                .build();
        request(request, iCallBack,tag);
    }

    @Override
    public void put(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag, boolean showLoading) {
        putJson(url, JSON.toJSONString(params),iCallBack,tag,showLoading);
    }

    @Override
    public void putJson(String url, String jsonString, ICallBack iCallBack, Integer tag, boolean showLoading) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonString);
        Request.Builder builder = new Request.Builder();
        final Request request = builder.tag(tag)
                .url(url)
                .put(requestBody)
                .build();
        request(request, iCallBack,tag);

    }

    @Override
    public void delete(String url, Map<String, Object> params, ICallBack iCallBack, Integer tag, boolean showLoading) {

    }

    @Override
    public void upload(final String url, final String paramName, final File file, final ICallBack iCallBack, final Integer tag, boolean showLoading) {
        MediaType MutilPart_Form_Data = MediaType.parse("multipart/form-data; charset=utf-8");
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
        requestBodyBuilder.setType(MultipartBody.FORM);
        requestBodyBuilder.addFormDataPart(paramName, file.getName(), RequestBody.create(MutilPart_Form_Data, file));

        RequestBody requestBody = requestBodyBuilder.build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.tag(tag)
                .url(url)
                .post(requestBody)
                .build();
        request(request, iCallBack,tag);
    }

    @Override
    public void upload(final String url, final String paramName, final List<File> files, final ICallBack iCallBack, final Integer tag,boolean showLoading) {
        MediaType MutilPart_Form_Data = MediaType.parse("multipart/form-data; charset=utf-8");
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
        requestBodyBuilder.setType(MultipartBody.FORM);
        for (File file : files) {
            requestBodyBuilder.addFormDataPart(paramName, file.getName(), RequestBody.create(MutilPart_Form_Data, file));
        }
        RequestBody requestBody = requestBodyBuilder.build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.tag(tag)
                .url(url)
                .post(requestBody)
                .build();
        request(request, iCallBack,tag);
    }


    private void request(final Request request, final ICallBack iCallBack, final int tag) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.isSuccessful()) {

                            emitter.onNext(response.body().string());
                        } else {
                            emitter.onError(new Throwable("res"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        iCallBack.onSuccess(s,tag);
                        Log.e("OkHttp","onNext");
                        iCallBack.onFinish(tag);
                    }

                    @Override
                    public void onError(Throwable e) {
                        iCallBack.onFailure(e,tag);
                        Log.e("OkHttp","onError"+e);
                        iCallBack.onFinish(tag);
                    }

                    @Override
                    public void onComplete() {
//                        iCallBack.onFinish();
                        Log.e("OkHttp","onComplete");
                    }
                });
    }

    @Override
    public void cancel(Integer tag) {
        cancelTag(tag);
    }

    @Override
    public void cancelAll() {
        okHttpClient.dispatcher().cancelAll();
    }


    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    public static FormBody.Builder  addParamToBuilder(Map<String,Object> map){
        FormBody.Builder builder=new FormBody.Builder();
//        if(!StringUtils.isEmpty(reqbody)){
//            if(reqbody.startsWith("?")){
//                reqbody=reqbody.substring(1);
//            }
//            String[] params=reqbody.split("&");
//            for(int i=0;i<params.length;i++){
//                if(params[i].equals("")){
//                    continue;
//                }
//                String [] kv=params[i].split("=");
//                builder.add(kv[0], kv[1]);
//            }
//        }
        if(map!=null){
            Iterator<Map.Entry<String,Object>> ite= map.entrySet().iterator();
            for(;ite.hasNext();){
                Map.Entry<String,Object> kv=ite.next();
                builder.add(kv.getKey(), kv.getValue().toString());
            }
        }
        return builder;
    }


    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}

