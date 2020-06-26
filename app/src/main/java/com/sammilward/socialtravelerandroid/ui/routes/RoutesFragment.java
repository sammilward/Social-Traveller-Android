package com.sammilward.socialtravelerandroid.ui.routes;

import android.app.AuthenticationRequiredException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.GetAllRoute;
import com.sammilward.socialtravelerandroid.http.routeManagement.JsonRouteMapper;
import com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers.RoutesGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class RoutesFragment extends Fragment {

    private TabLayout tlRoutes;
    private FloatingActionButton fabNewRoute;
    private RecyclerView rvRoutes;
    private RouteRecyclerAdapter routeRecyclerAdapter;
    private LinearLayoutManager routeLayoutManager;

    private RoutesGetEndpointInvoker routesGetEndpointInvoker;
    private JsonRouteMapper jsonRouteMapper;

    private static final String TAG = "RouteFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_routes, container, false);

        routesGetEndpointInvoker = new RoutesGetEndpointInvoker();
        jsonRouteMapper = new JsonRouteMapper();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseViews();
        displayRoutes(true);
    }

    private void initialiseViews()
    {
        tlRoutes = getView().findViewById(R.id.tlRoutes);
        routeLayoutManager = new LinearLayoutManager(getContext());
        rvRoutes = getView().findViewById(R.id.rvRoutes);
        rvRoutes.setLayoutManager(routeLayoutManager);
        fabNewRoute = getView().findViewById(R.id.fabNewRoute);

        fabNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewRouteActivity.class);
                startActivity(intent);
            }
        });

        tlRoutes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                {
                    displayRoutes(true);
                }
                else
                {
                    displayRoutes(false);
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

    private void displayRoutes(boolean popular)
    {
        try {
            if (popular)
            {
                routesGetEndpointInvoker.PrepareGetPopularRoutesGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ShowToast(getContext(), "Can not retrieve any routes currently");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful())
                        {
                            List<GetAllRoute> routes = jsonRouteMapper.jsonToRoutes(response);
                            routeRecyclerAdapter = new RouteRecyclerAdapter(getContext(), routes);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvRoutes.setVisibility(View.VISIBLE);
                                    rvRoutes.setAdapter(routeRecyclerAdapter);
                                }
                            });
                        }
                        else
                        {
                            ShowToast(getContext(), "No routes found");
                            rvRoutes.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            else
            {
                routesGetEndpointInvoker.PrepareGetUserRoutesGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ShowToast(getContext(), "Can not retrieve any routes currently");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful())
                        {
                            List<GetAllRoute> routes = jsonRouteMapper.jsonToRoutes(response);
                            routeRecyclerAdapter = new RouteRecyclerAdapter(getContext(), routes);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvRoutes.setVisibility(View.VISIBLE);
                                    rvRoutes.setAdapter(routeRecyclerAdapter);
                                }
                            });
                        }
                        else
                        {
                            ShowToast(getContext(), "No routes found");
                            rvRoutes.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tlRoutes.getSelectedTabPosition() == 0) displayRoutes(true);
        if (tlRoutes.getSelectedTabPosition() == 1) displayRoutes(false);
    }
}