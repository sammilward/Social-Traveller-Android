package com.sammilward.socialtravelerandroid.ui.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sammilward.socialtravelerandroid.http.userManagement.requests.CreateUserRequest;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserRegisterEndpointInvoker;
import com.sammilward.socialtravelerandroid.identity.IdentityServerError;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DEFAULT_LOCAL = "United Kingdom";

    private EditText txtEmail, txtUsername, txtFirstName, txtLastName, txtPassword, txtConfirmPassword;
    private Button btnRegister, btnChooseDate;
    private TextView lblUsersDOB;
    private Spinner spnBirthCountry;
    private DatePickerDialog datePickerDialog;
    private RadioButton rbMale, rbFemale;
    private UserRegisterEndpointInvoker userRegisterEndpointInvoker;

    private Date selectedDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRegisterEndpointInvoker = new UserRegisterEndpointInvoker();

        initialiseViews();
    }

    private void initialiseViews()
    {
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        lblUsersDOB = findViewById(R.id.lblUsersDOB);
        spnBirthCountry = findViewById(R.id.spnBirthCountry);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(valid())
                {
                    register();
                }
            }
        });

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDatePicker();
            }
        });

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, countries);
        spnBirthCountry.setAdapter(adapter);
        spnBirthCountry.setSelection(adapter.getPosition(DEFAULT_LOCAL));
    }

    private boolean valid()
    {
        if (!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void displayDatePicker() {
        datePickerDialog = new DatePickerDialog(RegisterActivity.this, RegisterActivity.this, 2002, 01, 01);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        selectedDOB = calendar.getTime();
        lblUsersDOB.setText(formatDate(selectedDOB, "dd-MM-yyyy"));
    }

    private String formatDate(Date date, String datePattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(date.getTime());
    }

    private void register()
    {
        CreateUserRequest request = new CreateUserRequest();
        request.Email = txtEmail.getText().toString();
        request.Username = txtUsername.getText().toString();
        request.FirstName = txtFirstName.getText().toString();
        request.LastName = txtLastName.getText().toString();
        request.Password = txtPassword.getText().toString();
        request.BirthCountry = spnBirthCountry.getSelectedItem().toString();
        request.DOB = formatDate(selectedDOB, "yyyy-MM-dd");
        if (rbMale.isChecked()) request.male = true;
        else request.male = false;

        userRegisterEndpointInvoker.PrepareCreateUserPost(TokenLifeManager.getInstance().getClientCredentialAccessToken(), request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ShowToast(getApplicationContext(), "Service is currently available.");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful())
                {
                    ShowToast(getApplicationContext(), "User successfully registered, please log in");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else
                {
                    Gson gson = new Gson();
                    String bodyString = response.body().string();
                    IdentityServerError[] identityServerErrors = gson.fromJson(bodyString, IdentityServerError[].class);
                    ShowToast(getApplicationContext(), identityServerErrors[0].descripton);
                }
            }
        });
    }
}
