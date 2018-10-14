package com.cdye.http;


/**
 * 定义请求回调接口
 *
 */

public interface ICallBack {
    void onSuccess(String result);
    void onFailure(Throwable throwable);
}
