package com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers;

import com.google.gson.Gson;
import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.requests.UpdateRouteLikeRequest;
import com.sammilward.socialtravelerandroid.http.routeManagement.requests.UpdateRouteUnlikeRequest;
import com.sammilward.socialtravelerandroid.http.userManagement.requests.UpdateUserRequest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RouteUpdateEndpointInvoker {
    private OkHttpClient client  = new OkHttpClient();
    private Gson gson = new Gson();
    private MediaType JSON = MediaType.parse("application/json");

    public void PrepareUpdateRoutePut(String accessToken, String routeId, boolean likeRoute, Callback callback){
        String url = App.getContext().getResources().getString(R.string.domainUrl) + App.getContext().getResources().getString(R.string.routeLocation) + "/" + routeId;

        String json;

        if (likeRoute)
        {
            UpdateRouteLikeRequest updateRouteLikeRequest = new UpdateRouteLikeRequest();
            updateRouteLikeRequest.like = true;
            json = gson.toJson(updateRouteLikeRequest);
        }
        else
        {
            UpdateRouteUnlikeRequest updateRouteUnlikeRequest = new UpdateRouteUnlikeRequest();
            updateRouteUnlikeRequest.unlike = true;
            json = gson.toJson(updateRouteUnlikeRequest);
        }

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken).put(body).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
