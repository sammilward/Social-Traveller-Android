package com.sammilward.socialtravelerandroid.ui.accountDetails;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserUpdateEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.userManagement.requests.UpdateUserRequest;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.home.HomeActivity;

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

public class AccountDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private UserUpdateEndpointInvoker userUpdateEndpointInvoker;
    private User user = null;
    private Date selectedDate;

    private EditText txtFirstName, txtLastName;
    private TextView lblUsersDOB;
    private Button btnChooseDate, btnUpdateProfile;
    private DatePickerDialog datePickerDialog;
    private Spinner spnBirthCountry;
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account_details, container, false);

        userUpdateEndpointInvoker = new UserUpdateEndpointInvoker();

        initialiseViews(root);

        user = CurrentUserManager.getInstance().getCurrentUser();

        displayAccountDetails(user);

        return root;
    }

    private void initialiseViews(View root)
    {
        txtFirstName = root.findViewById(R.id.txtFirstName);
        txtLastName = root.findViewById(R.id.txtLastName);
        lblUsersDOB = root.findViewById(R.id.lblUsersDOB);
        btnChooseDate = root.findViewById(R.id.btnChooseDate);
        btnUpdateProfile = root.findViewById(R.id.btnUpdateProfile);
        spnBirthCountry = root.findViewById(R.id.spnBirthCountry);

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
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        spnBirthCountry.setAdapter(adapter);

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDatePicker();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserDetails();
            }
        });
    }

    private void displayDatePicker() {
        datePickerDialog = new DatePickerDialog(getContext(),
                AccountDetailsFragment.this,
                user.getDOBAsLocalDate().getYear(),
                user.getDOBAsLocalDate().getMonthValue()-1,
                user.getDOBAsLocalDate().getDayOfMonth());
        datePickerDialog.show();
    }

    private void displayAccountDetails(User user)
    {
        txtFirstName.setText(user.firstName);
        txtLastName.setText(user.lastName);
        lblUsersDOB.setText(user.getDOBAsString("dd-MM-yyyy"));
        final String birthCountry = user.birthCountry;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spnBirthCountry.setSelection(adapter.getPosition(birthCountry));
            }
        });
    }

    private void updateUserDetails()
    {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        if (!user.firstName.equals(txtFirstName.getText().toString())) updateUserRequest.firstName = txtFirstName.getText().toString();
        if (!user.lastName.equals(txtLastName.getText().toString())) updateUserRequest.lastName = txtLastName.getText().toString();
        if (!user.birthCountry.equals(spnBirthCountry.getSelectedItem().toString())) updateUserRequest.birthCountry = spnBirthCountry.getSelectedItem().toString();
        if (!user.getDOBAsString("dd-MM-yyyy").equals(lblUsersDOB.getText().toString())) updateUserRequest.dob = formatDate(selectedDate, "yyyy-MM-dd");

        if (updateUserRequest.firstName != null || updateUserRequest.lastName != null || updateUserRequest.birthCountry != null || updateUserRequest.dob != null)
        {
            try {
                userUpdateEndpointInvoker.PrepareUpdateUserPut(TokenLifeManager.getInstance().getUserCredentialAccessToken(), updateUserRequest, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ShowToast(getContext(), "Account can not be updated right now.");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful())
                        {
                            ShowToast(getContext(), "Account details successfully updated.");
                            CurrentUserManager.getInstance().getUpdatedUser();
                        }
                        else
                        {
                            ShowToast(getContext(), "Error in the data you have entered");
                        }
                    }
                });
            } catch (RequiresAuthException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        selectedDate = calendar.getTime();

        lblUsersDOB.setText(formatDate(selectedDate, "dd-MM-yyyy"));
    }

    private String formatDate(Date date, String datePattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(date.getTime());
    }
}