package com.yizhiweather.app.util;

/*
 * HttpCallbackListener作为监听网络请求操作的接口，根据网络请求成功与否回调该接口中的
 * onFinish()方法或onError()方法
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);

}
