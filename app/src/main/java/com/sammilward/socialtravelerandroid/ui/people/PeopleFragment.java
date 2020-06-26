package com.sammilward.socialtravelerandroid.ui.people;

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
import com.sammilward.socialtravelerandroid.http.userManagement.JsonUserMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.http.userManagement.endpointInvokers.UsersGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class PeopleFragment extends Fragment {

    private TabLayout tlPeople;
    private RecyclerView rvPeople;
    private UserRecyclerAdapter userRecyclerAdapter;
    private LinearLayoutManager userLayoutManager;

    private UsersGetEndpointInvoker usersGetEndpointInvoker;
    private JsonUserMapper jsonUserMapper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_people, container, false);

        usersGetEndpointInvoker = new UsersGetEndpointInvoker();
        jsonUserMapper = new JsonUserMapper();

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseViews();
        displayUsers(true);
    }

    private void initialiseViews()
    {
        tlPeople = getView().findViewById(R.id.tlPeople);
        userLayoutManager = new LinearLayoutManager(getContext());
        rvPeople = getView().findViewById(R.id.rvPeople);
        rvPeople.setLayoutManager(userLayoutManager);

        tlPeople.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                {
                    displayUsers(true);
                }
                else
                {
                    displayUsers(false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void displayUsers(boolean travellers)
    {
        try {
            usersGetEndpointInvoker.PrepareGetUsersGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), travellers, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getContext(), "Can not retrieve any users currently");
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
                                rvPeople.setVisibility(View.VISIBLE);
                                rvPeople.setAdapter(userRecyclerAdapter);
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getContext(), "No users found");
                        rvPeople.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

}