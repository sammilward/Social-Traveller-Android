package com.sammilward.socialtravelerandroid.ui.people;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.Place;
import com.sammilward.socialtravelerandroid.http.routeManagement.Route;
import com.sammilward.socialtravelerandroid.http.userManagement.User;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserMapFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final float DEFAULT_ZOOM = 18.5F;
    private static final String TAG = "UserMapFragment";

    public UserMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SetupMap();
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {



            User displayedUser = bundle.getParcelable("user");
            initialiseUserMarker(displayedUser);
            setZoomToFitMarkers(displayedUser);
        }
    }

    private void SetupMap()
    {
        SetMapSettings();
    }

    private void SetMapSettings()
    {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMaxZoomPreference(DEFAULT_ZOOM);
    }

    private void initialiseUserMarker(User user)
    {
        mMap.addMarker(CreateMarkerOptionsForPlaces(user));
    }

    private MarkerOptions CreateMarkerOptionsForPlaces(User user)
    {
        IconGenerator iconGenerator = new IconGenerator(getContext());
        iconGenerator.setColor(Color.RED);
        iconGenerator.setTextAppearance(R.style.BlackText);

        MarkerOptions markerOptions = new MarkerOptions().title(user.firstName)
                .position(new LatLng(user.latitude, user.longitude));

        return markerOptions;
    }

    private void setZoomToFitMarkers(User user)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(user.latitude, user.longitude));
        builder.include(new LatLng(CurrentUserManager.getInstance().getCurrentUser().latitude, CurrentUserManager.getInstance().getCurrentUser().longitude));
        LatLngBounds bounds = builder.build();

        int padding = 50;
        final CameraUpdate cu;
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cu);
            }
        });
    }
}