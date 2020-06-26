package com.sammilward.socialtravelerandroid.http.routeManagement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sammilward.socialtravelerandroid.http.userManagement.User;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class JsonRouteMapper {

    private Gson gson;

    public JsonRouteMapper()
    {
        gson = new Gson();
    }

    public Route jsonToRoute(Response singleRouteResponse)
    {
        try {
            String jsonResponse = singleRouteResponse.body().string();
            return gson.fromJson(jsonResponse, Route.class);
        } catch (IOException e) {
            return null;
        }
    }

    public List<GetAllRoute> jsonToRoutes(Response multiRouteResponse)
    {
        try {
            String jsonResponse = multiRouteResponse.body().string();
            return gson.fromJson(jsonResponse, new TypeToken<List<GetAllRoute>>(){}.getType());
        } catch (IOException e) {
            return null;
        }
    }
}
