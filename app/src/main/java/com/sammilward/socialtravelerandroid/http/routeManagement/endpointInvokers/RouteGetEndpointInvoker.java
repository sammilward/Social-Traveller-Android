package com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RouteGetEndpointInvoker {
    private OkHttpClient client = new OkHttpClient();

    public void PrepareGetRouteGet(String accessToken, String routeId, Callback callback) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("RouteAPI")
                .addPathSegment("v1")
                .addPathSegment("route")
                .addPathSegment(routeId)
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
