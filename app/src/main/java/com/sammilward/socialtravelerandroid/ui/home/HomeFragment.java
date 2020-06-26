package com.sammilward.socialtravelerandroid.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.friendManagement.endpointInvokers.FriendsGetEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.userManagement.JsonUserMapper;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;
import com.sammilward.socialtravelerandroid.ui.friends.UsersProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class HomeFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private FriendsGetEndpointInvoker friendsGetEndpointInvoker;
    private JsonUserMapper jsonUserMapper;

    private List<User> friends;
    private HashMap<String, User> friendHashMap;

    private static final float DEFAULT_ZOOM = 18.5F;
    private static final String TAG = "MapsFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        friendsGetEndpointInvoker = new FriendsGetEndpointInvoker();
        jsonUserMapper = new JsonUserMapper();
        friends = new ArrayList<>();
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SetupMap();
        MoveCameraToCurrentLocation();
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void SetupMap()
    {
        setMapSettings();
        getFriendMarkers();
    }

    private void setMapSettings()
    {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setMaxZoomPreference(DEFAULT_ZOOM);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = null;
                if (friendHashMap.containsKey(marker.getId()))
                {
                    intent = new Intent(getContext(), UsersProfileActivity.class);
                    intent.putExtra("userId", friendHashMap.get(marker.getId()).id);
                }
                if (intent != null) startActivity(intent);
            }
        });
    }

    private void getFriendMarkers()
    {
        try {
            friendsGetEndpointInvoker.PrepareGetFriendsGet(TokenLifeManager.getInstance().getUserCredentialAccessToken(), false, false, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ShowToast(getContext(), "Could not fetch friends locations");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        friends = jsonUserMapper.jsonToUsers(response);
                        if (friends != null)
                        {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createFriendMarkers(friends);
                                }
                            });
                        }
                    }
                }
            });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }

    private void createFriendMarkers(List<User> friends)
    {
        friendHashMap = new HashMap<>();
        for (User friend: friends)
        {
            Marker marker = mMap.addMarker(createMarkerForFriend(friend));
            friendHashMap.put(marker.getId(), friend);
        }
    }

    private MarkerOptions createMarkerForFriend(User friend)
    {
        IconGenerator iconGenerator = new IconGenerator(getContext());
        iconGenerator.setColor(Color.RED);
        iconGenerator.setTextAppearance(R.style.BlackText);
        Bitmap bm = iconGenerator.makeIcon(friend.username);

        return new MarkerOptions().title(friend.firstName + " " + friend.lastName)
                .position(new LatLng(friend.latitude, friend.longitude)).snippet("From: " + friend.birthCountry)
                .icon(BitmapDescriptorFactory.fromBitmap(bm));
    }

    private void MoveCameraToCurrentLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Location currentLocation = (Location) task.getResult();
                        MoveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM );
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Could not get current location", Toast.LENGTH_SHORT);
                        Log.e(TAG, "Could not get current location");
                    }
                }
            });
        }catch(SecurityException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void MoveCamera(LatLng latLng, float zoom)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}