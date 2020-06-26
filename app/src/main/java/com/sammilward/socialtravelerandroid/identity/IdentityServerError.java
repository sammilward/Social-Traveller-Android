package com.sammilward.socialtravelerandroid.identity;

import com.google.gson.annotations.SerializedName;

public class IdentityServerError {
    @SerializedName("code")
    public String code;
    @SerializedName("description")
    public String descripton;
}
