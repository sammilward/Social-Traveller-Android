package com.sammilward.socialtravelerandroid.http;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpOkBackgroundRunner extends AsyncTask<Request, Void, String> {

    public static final OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Request... requests) {
        Response response = null;
        try {
            response = client.newCall(requests[0]).execute();
            if (!response.isSuccessful());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
