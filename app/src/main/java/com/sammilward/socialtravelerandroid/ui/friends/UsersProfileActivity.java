package com.sammilward.socialtravelerandroid.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendDeleteEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendRequestCreateEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendStatusGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendUpdateEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.JsonFriendStatusMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.JsonUserMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UserGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.people.UserMapFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class UsersProfileActivity extends AppCompatActivity {

    private TextView txtFirstName, txtLastName, txtAge, txtGender, txtCurrentLocation, txtBirthPlace;
    private UserGetEndpointInvoker userGetEndpointInvoker;
    private FriendStatusGetEndpointInvoker friendStatusGetEndpointInvoker;
    private FriendRequestCreateEndpointInvoker friendRequestCreateEndpointInvoker;
    private FriendUpdateEndpointInvoker friendUpdateEndpointInvoker;
    private FriendDeleteEndpointInvoker friendDeleteEndpointInvoker;
    private JsonUserMapper jsonUserMapper;
    private JsonFriendStatusMapper jsonFriendStatusMapper;

    private String userId;
    private int friendStatus = 5;

    private Menu storedMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);
        initialiseViews();

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        userGetEndpointInvoker = new UserGetEndpointInvoker();
        friendStatusGetEndpointInvoker = new FriendStatusGetEndpointInvoker();
        friendDeleteEndpointInvoker = new FriendDeleteEndpointInvoker();
        friendRequestCreateEndpointInvoker = new FriendRequestCreateEndpointInvoker();
        friendUpdateEndpointInvoker = new FriendUpdateEndpointInvoker();
        jsonUserMapper = new JsonUserMapper();
        jsonFriendStatusMapper = new JsonFriendStatusMapper();

        displayData();
    }

    public void initialiseViews()
    {
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtCurrentLocation = findViewById(R.id.txtCurrentLocation);
        txtBirthPlace = findViewById(R.id.txtBirthPlace);
    }

    private void displayData()
    {
        try {
            userGetEndpointInvoker.PrepareGetUserGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't retrieve information");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    final User user = jsonUserMapper.jsonToUser(response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtFirstName.setText(user.firstName);
                            txtLastName.setText(user.lastName);
                            txtAge.setText("Age: " + user.getAge());
                            if (user.male) txtGender.setText("Gender: Male");
                            else txtGender.setText("Gender: Female");
                            txtCurrentLocation.setText(user.currentCountry + ", " + user.currentCity);
                            txtBirthPlace.setText("From: " + user.birthCountry);

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("user", user);

                            UserMapFragment userMapFragment = new UserMapFragment();
                            userMapFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.userMapFragment, userMapFragment)
                                    .commit();


                        }
                    });
                }
            });

            getFriendStatus();
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        storedMenu = menu;
        if (friendStatus == 0) getMenuInflater().inflate(R.menu.friend_send_request, menu);
        if (friendStatus == 1) getMenuInflater().inflate(R.menu.remove_friend, menu);
        if (friendStatus == 2) {
            getMenuInflater().inflate(R.menu.friend_accept_request, menu);
            getMenuInflater().inflate(R.menu.friend_reject_request, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send_friend_request)
        {
            sendFriendRequest();
        }
        else if (id == R.id.action_remove_friend)
        {
            removeFriend();
        }
        else if (id == R.id.action_accept_friend_request)
        {
            acceptFriendRequest();
        }
        else if (id == R.id.action_reject_friend_request)
        {
            rejectFriendRequest();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendFriendRequest()
    {
        try {
            friendRequestCreateEndpointInvoker.PrepareCreateFriendReqeuestPost(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't send friend request right now");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Error when sending friend request.");
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void removeFriend()
    {
        try {
            friendDeleteEndpointInvoker.PrepareDeleteFriendDelete(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't remove friend right now.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Error when removing friend.");
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void acceptFriendRequest()
    {
        try {
            friendUpdateEndpointInvoker.PrepareUpdateFriendRequestAcceptPut(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't accept friend request right now");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Error when accepting friend request");
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void rejectFriendRequest()
    {
        try {
            friendUpdateEndpointInvoker.PrepareUpdateFriendRequestRejectPut(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't reject friend request right now");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Error when rejecting friend request");
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void getFriendStatus()
    {
        try {
            friendStatusGetEndpointInvoker.PrepareGetFriendStatusGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), userId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Can't redeem friend status");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    friendStatus = jsonFriendStatusMapper.jsonToFriendStatus(response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCreateOptionsMenu(storedMenu);
                        }
                    });
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }
}
