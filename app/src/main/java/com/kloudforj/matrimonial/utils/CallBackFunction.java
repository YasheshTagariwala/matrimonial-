package com.kloudforj.matrimonial.utils;

import com.squareup.okhttp.Response;

import java.io.IOException;

public interface CallBackFunction {
    void getResponseFromServer(Response response) throws IOException;
}
