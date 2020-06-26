package com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FriendsGetEndpointInvoker {
    private OkHttpClient client = new OkHttpClient();

    public void PrepareGetFriendsGet(String accessToken, boolean requests, boolean requested, Callback callback) {

        String trueOption = "true";

        HttpUrl.Builder httpBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("FriendAPI")
                .addPathSegment("v1")
                .addPathSegment("friends");

        HttpUrl httpUrl;

        if (requests)
        {
            httpUrl = httpBuilder.addQueryParameter("Requests", trueOption).build();
        }
        else if (requested)
        {
            httpUrl =  httpBuilder.addQueryParameter("Requested", trueOption).build();
        }
        else
        {
            httpUrl = httpBuilder.build();
        }

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
