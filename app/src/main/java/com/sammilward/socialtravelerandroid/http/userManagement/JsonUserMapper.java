package com.sammilward.socialtravelerandroid.http.userManagement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class JsonUserMapper {

    private Gson gson;

    public JsonUserMapper()
    {
        gson = new Gson();
    }

    public User jsonToUser(Response singleUserResponse)
    {
        try {
            String jsonResponse = singleUserResponse.body().string();
            return gson.fromJson(jsonResponse, User.class);
        } catch (IOException e) {
            return null;
        }
    }

    public List<User> jsonToUsers(Response multiUserResponse)
    {
        try {
            String jsonResponse = multiUserResponse.body().string();
            return gson.fromJson(jsonResponse, new TypeToken<List<User>>(){}.getType());
        } catch (IOException e) {
            return null;
        }
    }
}
