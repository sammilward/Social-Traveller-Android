package com.sammilward.socialtravelerandroid.ui.loader;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sammilward.socialtravelerandroid.ui.home.HomeActivity;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.login.LoginActivity;

public class InitialLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_loader);

        Intent intent;
        try {
            TokenLifeManager.getInstance().getUserCredentialAccessToken();
            intent = new Intent(getApplicationContext(), HomeActivity.class);

        } catch (RequiresAuthException e) {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
