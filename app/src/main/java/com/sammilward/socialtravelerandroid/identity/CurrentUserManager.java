package com.sammilward.socialtravelerandroid.identity;

import android.util.Log;

import com.sammilward.socialtravelerandroid.http.userManagement.JsonUserMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserGetEndpointInvoker;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CurrentUserManager {

    private static CurrentUserManager instance = null;
    private User currentUser;

    private UserGetEndpointInvoker userGetEndpointInvoker;
    private JsonUserMapper jsonUserMapper;

    private CurrentUserManager()
    {
        userGetEndpointInvoker = new UserGetEndpointInvoker();
        jsonUserMapper = new JsonUserMapper();
    }

    synchronized public static CurrentUserManager getInstance()
    {
        if (instance == null) instance = new CurrentUserManager();
        return  instance;
    }

    public User getCurrentUser()
    {
        if (currentUser == null)
        {
            try {
                userGetEndpointInvoker.PrepareGetUserGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("CurrentUserManager", "Could not get current user: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            currentUser = jsonUserMapper.jsonToUser(response);
                        }
                        else
                        {
                            Log.e("CurrentUserManager", "Could not get current user response failed: " + response.message());
                        }
                    }
                });
            } catch (RequiresAuthException e) {
                e.printStackTrace();
            }
        }

        while (currentUser == null){}
        return currentUser;
    }

    public User getUpdatedUser()
    {
        currentUser = null;
        return getCurrentUser();
    }

    public void removeCurrentUser()
    {
        currentUser = null;
    }
}
