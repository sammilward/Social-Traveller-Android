package com.sammilward.socialtravelerandroid.ui.routes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.JsonRouteMapper;
import com.sammilward.socialtravelerandroid.http.routeManagement.Route;
import com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers.RouteDeleteEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers.RouteGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers.RouteUpdateEndpointInvoker;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class RouteDetailsActivity extends AppCompatActivity {

    private RouteGetEndpointInvoker routeGetEndpointInvoker;
    private RouteDeleteEndpointInvoker routeDeleteEndpointInvoker;
    private RouteUpdateEndpointInvoker routeUpdateEndpointInvoker;
    private JsonRouteMapper jsonRouteMapper;
    private Route route;
    private String routeId;
    private String routeCreatorId;

    private Menu storedMenu;
    private ProgressBar pbRouteDetails;
    private Button btnStartRoute;
    private ImageView imgHiding;
    private RecyclerView rvPlaces;
    private PlaceRecyclerAdapter placeRecyclerAdapter;
    private LinearLayoutManager placesLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        routeGetEndpointInvoker = new RouteGetEndpointInvoker();
        routeDeleteEndpointInvoker = new RouteDeleteEndpointInvoker();
        routeUpdateEndpointInvoker = new RouteUpdateEndpointInvoker();
        jsonRouteMapper = new JsonRouteMapper();

        Intent intent = getIntent();
        routeId = intent.getStringExtra("routeId");
        routeCreatorId = intent.getStringExtra("routeCreatorId");

        initialiseViews();

        displayData(routeId);
    }

    private void initialiseViews()
    {
        placesLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvPlaces = findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(placesLayoutManager);
        pbRouteDetails = findViewById(R.id.pbRouteDetails);
        imgHiding = findViewById(R.id.imgHiding);
        btnStartRoute = findViewById(R.id.btnStartRoute);

        btnStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("route", route);
                bundle.putBoolean("showLines", true);

                PlacesMapFragment placesMapFragment = new PlacesMapFragment();
                placesMapFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeMapFragment, placesMapFragment)
                        .commit();

                btnStartRoute.setVisibility(View.GONE);
            }
        });
    }

    private void displayData(String routeId)
    {
        try {
            routeGetEndpointInvoker.PrepareGetRouteGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), routeId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Could not fetch route details right now.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        route = jsonRouteMapper.jsonToRoute(response);
                        placeRecyclerAdapter = new PlaceRecyclerAdapter(getApplicationContext(), route.places);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvPlaces.setAdapter(placeRecyclerAdapter);

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("route", route);
                                bundle.putBoolean("showLines", false);

                                PlacesMapFragment placesMapFragment = new PlacesMapFragment();
                                placesMapFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.placeMapFragment, placesMapFragment)
                                        .commit();

                                getSupportActionBar().setTitle(route.name);
                                pbRouteDetails.setVisibility(View.GONE);
                                onCreateOptionsMenu(storedMenu);
                                imgHiding.setVisibility(View.GONE);
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Could not fetch route details right now. API Threw error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void deleteRoute()
    {
        try {
            routeDeleteEndpointInvoker.PrepareDeleteRouteDelete(TokenLifeManager.getInstance().getUserCredentialAccessToken(), routeId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Delete route failed, no connection");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        ShowToast(getApplicationContext(), "Route successfully deleted.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        ShowToast(getApplicationContext(), "Route could not be deleted. API threw error.");
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        storedMenu = menu;
        if (route == null) return true;
        if (CurrentUserManager.getInstance().getCurrentUser().id.equals(routeCreatorId)) getMenuInflater().inflate(R.menu.route_details_owner, menu);
        if (route.userLikes) getMenuInflater().inflate(R.menu.route_details_non_owner_unlike, menu);
        else getMenuInflater().inflate(R.menu.route_details_non_owner_like, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteRoute();
            return true;
        }
        else if (id == R.id.action_like)
        {
            if (route.userLikes)
            {
                implementLikeAction(false, item);
            }
            else
            {
                implementLikeAction(true, item);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void implementLikeAction(final boolean like, final MenuItem item)
    {
        try {
            routeUpdateEndpointInvoker.PrepareUpdateRoutePut(TokenLifeManager.getInstance().getUserCredentialAccessToken(), route.id, like, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getApplicationContext(), "Could not rate route, can't make connection.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        if (like)
                        {
                            ShowToast(getApplicationContext(), "Liked route");
                            route.userLikes = true;
                        }
                        else
                        {
                            ShowToast(getApplicationContext(), "Unliked route");
                            route.userLikes = false;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (like) item.setTitle("Unlike");
                                else item.setTitle("Like");
                            }
                        });
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }
}
