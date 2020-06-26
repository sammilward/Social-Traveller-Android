package com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers;

import com.google.gson.Gson;
import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.requests.CreateRouteRequest;
import com.sammilward.socialtravelerandroid.http.userManagement.requests.CreateUserRequest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RouteCreateEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();
    private Gson gson = new Gson();
    private MediaType JSON = MediaType.parse("application/json");

    public void PrepareCreateRoutePost(String accessToken, CreateRouteRequest createRouteRequest, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.routeLocation);

        String json = gson.toJson(createRouteRequest);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).post(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
