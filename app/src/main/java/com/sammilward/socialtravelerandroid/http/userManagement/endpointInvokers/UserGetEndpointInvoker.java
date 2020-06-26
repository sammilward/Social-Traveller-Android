package com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers;

import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UserGetEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();

    public void PrepareGetUserGet(String accessToken, String userId, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.userLocation) + "/" + userId;
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void PrepareGetUserGet(String accessToken, Callback callback){
        PrepareGetUserGet(accessToken, "usingtoken", callback);
    }
}
