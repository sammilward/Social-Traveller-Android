package com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers;

import com.google.gson.Gson;
import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FriendRequestCreateEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();
    private Gson gson = new Gson();
    private MediaType JSON = MediaType.parse("application/json");

    public void PrepareCreateFriendReqeuestPost(String accessToken, String userId, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.friendLocation);

        NewFriendRequestRequest newFriendRequestRequest = new NewFriendRequestRequest();
        newFriendRequestRequest.requestedUserId = userId;

        String json = gson.toJson(newFriendRequestRequest);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).post(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
