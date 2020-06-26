package com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FriendStatusGetEndpointInvoker {
    private OkHttpClient client = new OkHttpClient();

    public void PrepareGetFriendStatusGet(String accessToken, String userId, Callback callback) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("FriendAPI")
                .addPathSegment("v1")
                .addPathSegment("friendstatus")
                .addPathSegment(userId)
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
