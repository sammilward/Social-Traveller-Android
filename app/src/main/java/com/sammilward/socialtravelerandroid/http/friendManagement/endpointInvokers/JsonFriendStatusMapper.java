package com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sammilward.socialtravelerandroid.http.userManagement.User;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class JsonFriendStatusMapper {

    private Gson gson;

    public JsonFriendStatusMapper()
    {
        gson = new Gson();
    }

    public int jsonToFriendStatus(Response friendStatusResponse)
    {
        try {
            String jsonResponse = friendStatusResponse.body().string();
            return gson.fromJson(jsonResponse, Integer.class);
        } catch (IOException e) {
            return 5;
        }
    }
}
