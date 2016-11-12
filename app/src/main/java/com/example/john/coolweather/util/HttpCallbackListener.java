package com.example.john.coolweather.util;

/**
 * Created by John on 2016/11/12.
 * 网络请求的回调方法
 * 作用：
 * 数据获取成功或失败时调用
 */

public interface HttpCallbackListener {
    /**
     * 成功时，回调方法
     *
     * @param data
     */
    void onSuccess(String data);

    /**
     * 出现异常时，回调方法
     *
     * @param e
     */
    void onError(Exception e);
}
