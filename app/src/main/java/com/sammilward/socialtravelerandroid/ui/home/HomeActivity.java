package com.sammilward.socialtravelerandroid.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserDeleteEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserUpdateEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.userManagement.requests.UpdateUserRequest;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.login.LoginActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private UserDeleteEndpointInvoker userDeleteEndpointInvoker;
    private UserUpdateEndpointInvoker userUpdateEndpointInvoker;
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private NavigationView navigationView;

    private boolean onResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = findViewById(R.id.nav_view);

        userDeleteEndpointInvoker = new UserDeleteEndpointInvoker();
        userUpdateEndpointInvoker = new UserUpdateEndpointInvoker();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        checkLocationPermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_people, R.id.nav_friends, R.id.nav_routes, R.id.nav_settings, R.id.nav_account_details, R.id.nav_logout, R.id.nav_remove_account)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                logout();
                return false;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_remove_account).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                removeAccount();
                return false;
            }
        });
    }

    private void checkLocationPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_LOCATION);
        }
        else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            updateUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateUserLocation();
                } else {
                    ShowToast(getApplicationContext(), "This application requires location to show local travellers");
                    logout();
                }
                return;
            }
        }
    }

    private void updateUserLocation()
    {
        Task userLocation = mFusedLocationProviderClient.getLastLocation();
        userLocation.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Location currentLocation = (Location) task.getResult();

                UpdateUserRequest updateUserRequest = new UpdateUserRequest();
                updateUserRequest.latitude = currentLocation.getLatitude();
                updateUserRequest.longitude = currentLocation.getLongitude();

                try {
                    userUpdateEndpointInvoker.PrepareUpdateUserPut(TokenLifeManager.getInstance().getUserCredentialAccessToken(), updateUserRequest, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            ShowToast(getApplicationContext(), "Could not update user location. Request Failed.");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful())
                            {
                                ShowToast(getApplicationContext(), "Could not update user location. API returned error.");
                            }
                            else
                            {
                                CurrentUserManager.getInstance().getUpdatedUser();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displayUserDataNavHeader();
                                    }
                                });
                            }
                        }
                    });
                } catch (RequiresAuthException e) {
                    logout();
                }
            }
        });
    }

    public void displayUserDataNavHeader()
    {
        View header = navigationView.getHeaderView(0);
        TextView txtNames = header.findViewById(R.id.lblNames);
        TextView txtBirthCountry = header.findViewById(R.id.lblBirthCountry);
        txtNames.setText(CurrentUserManager.getInstance().getCurrentUser().firstName + " " + CurrentUserManager.getInstance().getCurrentUser().lastName);
        txtBirthCountry.setText(CurrentUserManager.getInstance().getCurrentUser().birthCountry);
    }

    private void logout()
    {
        TokenLifeManager.getInstance().removeTokens();
        CurrentUserManager.getInstance().removeCurrentUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void removeAccount()
    {
        try {
            userDeleteEndpointInvoker.PrepareDeleteUserDelete(TokenLifeManager.getInstance().getUserCredentialAccessToken(), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Account can not be removed at this current time.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ShowToast(getApplicationContext(), "Users account successfully removed.");
                    logout();
                }
            });
        } catch (RequiresAuthException e) {
            logout();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onResume = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onResume) checkLocationPermissions();
        onResume = false;
    }
}
