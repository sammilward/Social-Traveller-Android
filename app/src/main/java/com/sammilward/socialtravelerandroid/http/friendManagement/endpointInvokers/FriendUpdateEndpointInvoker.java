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

public class FriendUpdateEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();
    private Gson gson = new Gson();
    private MediaType JSON = MediaType.parse("application/json");

    public void PrepareUpdateFriendRequestAcceptPut(String accessToken, String userId, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.friendLocation) + "/" + userId;

        AcceptFriendRequestRequest acceptFriendRequestRequest = new AcceptFriendRequestRequest();
        acceptFriendRequestRequest.accept = true;

        String json = gson.toJson(acceptFriendRequestRequest);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).put(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void PrepareUpdateFriendRequestRejectPut(String accessToken, String userId, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.friendLocation) + "/" + userId;

        RejectFriendRequestRequest rejectFriendRequestRequest = new RejectFriendRequestRequest();
        rejectFriendRequestRequest.reject = true;

        String json = gson.toJson(rejectFriendRequestRequest);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).put(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
