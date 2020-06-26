package com.sammilward.socialtravelerandroid.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendsGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.userManagement.JsonUserMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.people.UserRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class FriendsFragment extends Fragment {

    private TabLayout tlFriends;
    private RecyclerView rvFriends;
    private UserRecyclerAdapter userRecyclerAdapter;
    private LinearLayoutManager userLayoutManager;

    private FriendsGetEndpointInvoker friendsGetEndpointInvoker;
    private JsonUserMapper jsonUserMapper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsGetEndpointInvoker = new FriendsGetEndpointInvoker();
        jsonUserMapper = new JsonUserMapper();

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseViews();
        displayUsers(false, false);
    }

    private void initialiseViews()
    {
        tlFriends = getView().findViewById(R.id.tlFriends);
        userLayoutManager = new LinearLayoutManager(getContext());
        rvFriends = getView().findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(userLayoutManager);

        tlFriends.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) displayUsers(false, false);
                else if(tab.getPosition() == 1) displayUsers(true, false);
                else displayUsers(false, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void displayUsers(boolean requests, boolean requested)
    {
        try {
            friendsGetEndpointInvoker.PrepareGetFriendsGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), requests, requested, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getContext(), "Can not retrieve any friends currently");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        List<User> users = jsonUserMapper.jsonToUsers(response);
                        userRecyclerAdapter = new UserRecyclerAdapter(getContext(), users);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvFriends.setVisibility(View.VISIBLE);
                                rvFriends.setAdapter(userRecyclerAdapter);
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getContext(), "None found");
                        rvFriends.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tlFriends.getSelectedTabPosition() == 0) displayUsers(false, false);
        else if(tlFriends.getSelectedTabPosition() == 1) displayUsers(true, false);
        else displayUsers(false, true);
    }
}