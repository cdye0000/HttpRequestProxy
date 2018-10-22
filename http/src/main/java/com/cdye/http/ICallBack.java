package com.cdye.http;


/**
 * 定义请求回调接口
 *
 */

public interface ICallBack {
    void onSuccess(String result,int what);
    void onFailure(Throwable throwable, int what);
    void onFinish(int what);
}
