package com.sammilward.socialtravelerandroid.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sammilward.socialtravelerandroid.ui.home.HomeActivity;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button cmdLogin;
    private TextView lblRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initaliseViews();
    }

    private void initaliseViews()
    {
        txtEmail = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        cmdLogin = findViewById(R.id.btnLogin);
        lblRegister = findViewById(R.id.lblRegister);

        cmdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = TokenLifeManager.getInstance().getUserCredentialAccessTokenWithUserAuth(txtEmail.getText().toString(), txtPassword.getText().toString());
                if (token == null)
                {
                    Toast.makeText(getApplicationContext(), "email or password is incorrect, try again.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        });

        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
