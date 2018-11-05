package com.kloudforj.matrimonial.utils;

import java.io.IOException;

import okhttp3.Response;

public interface CallBackFunction {
    void getResponseFromServer(Response response) throws IOException;
}
