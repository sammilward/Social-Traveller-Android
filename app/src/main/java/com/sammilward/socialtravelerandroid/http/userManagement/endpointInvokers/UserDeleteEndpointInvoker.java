package com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers;

import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UserDeleteEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();

    public void PrepareDeleteUserDelete(String accessToken, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.userLocation);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).delete().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
