package com.spike.bot.listener;

/**
 * Created by Sagar on 6/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public interface ResponseErrorCode {
    void onLogout();
    void onProgress();
    void onSuccess();
    void onErrorCode(int errorResponseCode);
}
