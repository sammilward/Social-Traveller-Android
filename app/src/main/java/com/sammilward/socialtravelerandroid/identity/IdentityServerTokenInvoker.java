package com.sammilward.socialtravelerandroid.identity;

import com.sammilward.socialtravelerandroid.App;
import com.sammilward.socialtravelerandroid.http.HttpOkBackgroundRunner;
import com.sammilward.socialtravelerandroid.R;

import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class IdentityServerTokenInvoker {
    private static final String url = App.getContext().getResources().getString(R.string.domainUrl) +  App.getContext().getResources().getString(R.string.identityLocation) + App.getContext().getResources().getString(R.string.tokenEndpoint);
    private static final String contentType = "Content-Type";
    private static final String header = "application/x-www-form-urlencoded";

    public static String GetClientCredentialAuthenticationResponseString()
    {
        String grant_type = "client_credentials";
        String scope = "UserAPI";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", grant_type)
                .add("client_id", App.getContext().getResources().getString(R.string.clientId))
                .add("client_secret", App.getContext().getResources().getString(R.string.clientSecret))
                .add("scope", scope)
                .build();

        Request request = new Request.Builder().url(url).header(contentType, header).post(formBody).build();

        String response = null;

        try {
            response = new HttpOkBackgroundRunner().execute(request).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String GetUserCredentialAuthenticationResponseString(String username, String password)
    {
        String grant_type = "password";
        String scope = "UserAPI RouteAPI FriendAPI offline_access";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", grant_type)
                .add("client_id", App.getContext().getResources().getString(R.string.clientId))
                .add("client_secret", App.getContext().getResources().getString(R.string.clientSecret))
                .add("username", username)
                .add("password", password)
                .add("scope", scope)
                .build();

        Request request = new Request.Builder().url(url).header(contentType, header).post(formBody).build();

        String response = null;

        try {
            response = new HttpOkBackgroundRunner().execute(request).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String GetUserCredentialRefreshTokenAuthenticationResponseString(String refreshToken)
    {
        String grant_type = "refresh_token";

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", grant_type)
                .add("client_id", App.getContext().getResources().getString(R.string.clientId))
                .add("client_secret", App.getContext().getResources().getString(R.string.clientSecret))
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder().url(url).header(contentType, header).post(formBody).build();

        String response = null;

        try {
            response = new HttpOkBackgroundRunner().execute(request).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }
}

