package com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.sammilward.socialtravelerandroid.App;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UsersGetEndpointInvoker {
    private OkHttpClient client = new OkHttpClient();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public void PrepareGetUsersGet(String accessToken, boolean travellers, Callback callback) {
        String minAge = sharedPreferences.getString("minAge", "0");
        String maxAge = sharedPreferences.getString("maxAge", "100");
        String prefferedGender = sharedPreferences.getString("preferredGender", "Both");

        String genderOption = null;
        if (prefferedGender.equals("Both")) genderOption = "2";
        if (prefferedGender.equals("Male")) genderOption = "1";
        if (prefferedGender.equals("Female")) genderOption = "0";

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("thesocialtraveler.ddns.net")
                .addPathSegment("UserAPI")
                .addPathSegment("v1")
                .addPathSegment("users")
                .addQueryParameter("Travellers", String.valueOf(travellers))
                .addQueryParameter("MinAge", minAge)
                .addQueryParameter("MaxAge", maxAge)
                .addQueryParameter("GenderOption", genderOption)
                .build();

        Request request = new Request.Builder().url(httpUrl).header("Authorization", "Bearer " + accessToken).get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
