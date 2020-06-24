package com.spike.bot.api_retrofit;

/*dev arp add this interface on 23 june 2020*/

public interface DataResponseListener {
    void onData_SuccessfulResponse(String stringResponse);

    void onData_FailureResponse();
}