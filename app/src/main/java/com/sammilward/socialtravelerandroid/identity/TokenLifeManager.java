package com.sammilward.socialtravelerandroid.identity;

import android.content.Context;
import android.content.SharedPreferences;

import com.sammilward.socialtravelerandroid.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

public class TokenLifeManager {

    private static TokenLifeManager instance = new TokenLifeManager();

    private final long REFRESHTOKENEXPIRYTIMESECONDS = 2592000L;
    private final String TokenPrefFile = "TokenPrefs";
    private SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(TokenPrefFile, Context.MODE_PRIVATE);
    private SharedPreferences.Editor editor = sharedPreferences.edit();

    private final String clientAccessTokenKey = "CATK";
    private final String clientExpiryTimeKey = "CETK";
    private final String userAccessTokenKey = "UATK";
    private final String userExpiryTimeKey = "UETK";
    private final String refreshTokenKey = "RTK";
    private final String refreshExpiryTimeKey = "RTEK";

    public static TokenLifeManager getInstance()
    {
        if (instance == null)
        {
            instance = new TokenLifeManager();
        }
        return instance;
    }

    public String getClientCredentialAccessToken()
    {
        String token = sharedPreferences.getString(clientAccessTokenKey, null);

        long clientExpiryTime = sharedPreferences.getLong(clientExpiryTimeKey, 0L);
        if (clientExpiryTime < Instant.now().getEpochSecond())
        {
            String response = IdentityServerTokenInvoker.GetClientCredentialAuthenticationResponseString();
            if (response != null)
            {
                try {
                    JSONObject jObject = new JSONObject(response);
                    String accessToken = jObject.getString("access_token");
                    Long secondsTillExpire = jObject.getLong("expires_in");
                    storeSharedPrefToken(clientAccessTokenKey, accessToken);
                    storeSharedPrefExpiryTime(clientExpiryTimeKey, secondsTillExpire);
                    token = accessToken;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return token;
    }

    public String getUserCredentialAccessToken() throws RequiresAuthException {
        String token = sharedPreferences.getString(userAccessTokenKey, null);

        long userExpiryTime = sharedPreferences.getLong(userExpiryTimeKey, 0L);
        if (userExpiryTime < Instant.now().getEpochSecond())
        {
            long refreshExpiryTime = sharedPreferences.getLong(refreshExpiryTimeKey, 0L);
            if (refreshExpiryTime < Instant.now().getEpochSecond())
            {
                throw new RequiresAuthException();
            }
            else
            {
                String refreshToken = sharedPreferences.getString(refreshTokenKey, null);
                String response = IdentityServerTokenInvoker.GetUserCredentialRefreshTokenAuthenticationResponseString(refreshToken);

                if (response != null)
                {
                    try {
                        JSONObject jObject = new JSONObject(response);
                        String accessToken = jObject.getString("access_token");
                        String newRefreshToken = jObject.getString("refresh_token");
                        Long secondsTillExpire = jObject.getLong("expires_in");
                        storeSharedPrefToken(userAccessTokenKey, accessToken);
                        storeSharedPrefToken(refreshTokenKey, newRefreshToken);
                        storeSharedPrefExpiryTime(userExpiryTimeKey, secondsTillExpire);
                        storeSharedPrefExpiryTime(refreshExpiryTimeKey, REFRESHTOKENEXPIRYTIMESECONDS);
                        token = accessToken;
                    } catch (JSONException e) {
                        throw new RequiresAuthException();
                    }
                }
            }
        }
        return token;
    }

    public String getUserCredentialAccessTokenWithUserAuth(String username, String password)
    {
        String token = null;

        String response = IdentityServerTokenInvoker.GetUserCredentialAuthenticationResponseString(username, password);

        if (response != null)
        {
            try {
                JSONObject jObject = new JSONObject(response);
                String accessToken = jObject.getString("access_token");
                String newRefreshToken = jObject.getString("refresh_token");
                Long secondsTillExpire = jObject.getLong("expires_in");
                storeSharedPrefToken(userAccessTokenKey, accessToken);
                storeSharedPrefToken(refreshTokenKey, newRefreshToken);
                storeSharedPrefExpiryTime(userExpiryTimeKey, secondsTillExpire);
                storeSharedPrefExpiryTime(refreshExpiryTimeKey, REFRESHTOKENEXPIRYTIMESECONDS);
                token = accessToken;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return token;
    }

    public void removeTokens()
    {
        editor.remove(userAccessTokenKey);
        editor.remove(refreshTokenKey);
        editor.remove(userExpiryTimeKey);
        editor.remove(refreshExpiryTimeKey);
        editor.apply();
    }

    private void storeSharedPrefToken(String key, String value)
    {
        editor.putString(key, value);
        editor.apply();
    }

    private void storeSharedPrefExpiryTime(String key, Long secondsTillExpire)
    {
        Long value = Instant.now().getEpochSecond() + secondsTillExpire;
        editor.putLong(key, value);
        editor.apply();
    }
}
