package com.sammilward.socialtravelerandroid.ui.routes;

import android.Manifest;
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
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.IconGenerator;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.Place;
import com.sammilward.socialtravelerandroid.http.routeManagement.Route;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlacesMapFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private Route route;

    private GoogleMap mMap;

    private static final float DEFAULT_ZOOM = 18.5F;
    private static final String TAG = "MapsFragment";
    private List<Marker> placeMarkers = new ArrayList<>();

    public PlacesMapFragment() {
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
            route = bundle.getParcelable("route");
            initialisePlaceMarkers(route.places);
            if (bundle.getBoolean("showLines")) initialiseRouteMarkings(route.routeCoords);
            setZoomToFitMarkers();
        }
    }

    private void initialiseRouteMarkings(double[] routeCoords) {

        List<LatLng> points = new ArrayList<>();

        for (int i = 0; i < routeCoords.length; i = i + 2)
        {
            points.add(new LatLng(routeCoords[i], routeCoords[i+1]));
        }

        PolylineOptions options = new PolylineOptions();
        options.addAll(points);
        options.geodesic(true);

        mMap.addPolyline(options);
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

    private void initialisePlaceMarkers(List<Place> places)
    {
        for (int i = 0; i < places.size(); i++)
        {
            Marker marker = mMap.addMarker(CreateMarkerOptionsForPlaces(places.get(i), i));
            placeMarkers.add(marker);
        }
    }

    private MarkerOptions CreateMarkerOptionsForPlaces(Place place, int i)
    {
        DecimalFormat df = new DecimalFormat("0.0");

        IconGenerator iconGenerator = new IconGenerator(getContext());
        iconGenerator.setColor(Color.RED);
        iconGenerator.setTextAppearance(R.style.BlackText);
        Bitmap bm = iconGenerator.makeIcon(String.valueOf(i+1));

        MarkerOptions markerOptions = new MarkerOptions().title(place.name)
                .position(new LatLng(place.latitude, place.longitude)).snippet("Rating: " + df.format(place.rating))
                .icon(BitmapDescriptorFactory.fromBitmap(bm));
        return markerOptions;
    }

    private void setZoomToFitMarkers()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : placeMarkers) {
            builder.include(marker.getPosition());
        }
        builder.include(new LatLng(CurrentUserManager.getInstance().getCurrentUser().latitude, CurrentUserManager.getInstance().getCurrentUser().longitude));
        LatLngBounds bounds = builder.build();

        int padding = 50;

        final CameraUpdate cu;
        if (placeMarkers.size() == 1)
        {
            cu = CameraUpdateFactory.newLatLng(placeMarkers.get(1).getPosition());
        }
        else
        {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cu);
            }
        });
    }
}