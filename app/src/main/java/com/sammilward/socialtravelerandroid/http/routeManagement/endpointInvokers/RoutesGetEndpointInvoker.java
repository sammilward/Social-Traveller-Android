package com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RoutesGetEndpointInvoker {
    private OkHttpClient client = new OkHttpClient();

    public void PrepareGetUserRoutesGet(String accessToken, Callback callback) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("RouteAPI")
                .addPathSegment("v1")
                .addPathSegment("routes")
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void PrepareGetPopularRoutesGet(String accessToken, Callback callback) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("RouteAPI")
                .addPathSegment("v1")
                .addPathSegment("routes")
                .addQueryParameter("Popular", String.valueOf(true))
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void PrepareGetFriendsRoutesGet(String accessToken, String friendId, Callback callback) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("RouteAPI")
                .addPathSegment("v1")
                .addPathSegment("routes")
                .addQueryParameter("FriendId", friendId)
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
