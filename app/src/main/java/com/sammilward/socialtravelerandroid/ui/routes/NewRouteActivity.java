package com.sammilward.socialtravelerandroid.ui.routes;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.GeoApiContext;
import com.google.maps.ImageResult;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.Place;
import com.sammilward.socialtravelerandroid.http.routeManagement.endpointInvokers.RouteCreateEndpointInvoker;
import com.sammilward.socialtravelerandroid.http.routeManagement.requests.CreateRouteRequest;
import com.sammilward.socialtravelerandroid.identity.CurrentUserManager;
import com.sammilward.socialtravelerandroid.identity.RequiresAuthException;
import com.sammilward.socialtravelerandroid.identity.TokenLifeManager;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sammilward.socialtravelerandroid.ui.helpers.DisplayOnThread.ShowToast;

public class NewRouteActivity extends AppCompatActivity {

    private EditText txtRouteName;
    private ImageView imgPlacePhoto;
    private FloatingActionButton fabCross, fabTick;
    private TextView lblPlaceName, lblOpenNow, lblPlaceType, lblRatingScore, lblRatingNumberOfReviews, lblPlaceCounter;
    private Button btnCreateRoute;

    private PlacesSearchResponse currentPlacesResponse;
    private int currentPlacePosition;

    private GeoApiContext geoApiContext;
    private PlacesSearchResult currentPlace;
    private PlacesSearchResult nextPlace;
    private ImageResult currentPlaceImage;
    private ImageResult nextPlaceImage;

    private final int maxLimitOfPlaces = 8;

    private List<PlacesSearchResult> selectedPlaces = new ArrayList<>();
    private List<Place> selectedPlace = new ArrayList<>();

    private RouteCreateEndpointInvoker routeCreateEndpointInvoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        routeCreateEndpointInvoker = new RouteCreateEndpointInvoker();

        initialiseViews();

        loadPlaces();
        displayPlace();
    }

    private void initialiseViews()
    {
        txtRouteName = findViewById(R.id.txtRouteName);
        imgPlacePhoto = findViewById(R.id.imgPlacePhoto);
        imgPlacePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        fabCross = findViewById(R.id.fabCross);
        fabTick = findViewById(R.id.fabTick);
        lblPlaceName = findViewById(R.id.lblPlaceName);
        lblOpenNow = findViewById(R.id.lblOpenNow);
        lblPlaceType = findViewById(R.id.lblPlaceType);
        lblRatingScore = findViewById(R.id.lblRatingScore);
        lblRatingNumberOfReviews = findViewById(R.id.lblRatingNumberOfReviews);
        lblPlaceCounter = findViewById(R.id.lblPlaceCounter);
        btnCreateRoute = findViewById(R.id.btnCreateRoute);

        fabCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPlaces.size() < maxLimitOfPlaces)
                {
                    showNextPlace();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Max places already selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPlaces.size() < maxLimitOfPlaces)
                {
                    saveSelectedPlace();
                    showNextPlace();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Max places already selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute();
            }
        });
    }

    private void loadPlaces()
    {
        currentPlacePosition = 0;

        GeoApiContext.Builder builder = new GeoApiContext.Builder();
        geoApiContext = builder.apiKey("APIKEY").build();

        LatLng latLng = new LatLng(CurrentUserManager.getInstance().getCurrentUser().latitude,CurrentUserManager.getInstance().getCurrentUser().longitude);

        try {
            currentPlacesResponse = PlacesApi.textSearchQuery(geoApiContext, "things to do", latLng).radius(2000).await();
            currentPlace = currentPlacesResponse.results[currentPlacePosition];
            currentPlaceImage = PlacesApi.photo(geoApiContext, currentPlacesResponse.results[currentPlacePosition].photos[0].photoReference).maxHeight(1000).maxWidth(1000).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaces(String nextPageToken)
    {
        try {
            currentPlacePosition = 0;
            currentPlacesResponse = PlacesApi.textSearchNextPage(geoApiContext, nextPageToken).await();
            nextPlace = currentPlacesResponse.results[currentPlacePosition];
            nextPlaceImage = PlacesApi.photo(geoApiContext, currentPlacesResponse.results[currentPlacePosition].photos[0].photoReference).maxHeight(1000).maxWidth(1000).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNextPlace()
    {
        currentPlace = nextPlace;
        currentPlaceImage = nextPlaceImage;
        displayPlace();
    }

    private void saveSelectedPlace()
    {
        selectedPlaces.add(currentPlace);

        Place place = new Place();
        place.placeId = currentPlace.placeId;
        place.name = currentPlace.name;
        place.photoReference = currentPlace.photos[0].photoReference;
        place.latitude = currentPlace.geometry.location.lat;
        place.longitude = currentPlace.geometry.location.lng;
        place.rating = currentPlace.rating;
        place.numberOfRatings = currentPlace.userRatingsTotal;
        place.types = Arrays.asList(currentPlace.types);
        place.imageContent = Base64.encodeToString(currentPlaceImage.imageData, Base64.DEFAULT);
        selectedPlace.add(place);

        updatePlaceCounterLabel();
    }

    private void updatePlaceCounterLabel()
    {
        lblPlaceCounter.setText(selectedPlaces.size() + " / " + maxLimitOfPlaces);
    }

    private void displayPlace()
    {
        if (currentPlaceImage != null)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(currentPlaceImage.imageData, 0, currentPlaceImage.imageData.length);
            imgPlacePhoto.setImageBitmap(Bitmap.createScaledBitmap(bmp, 1500,1500, false));
        }
        else
        {
            imgPlacePhoto.setImageResource(android.R.color.transparent);
        }

        lblPlaceName.setText(currentPlace.name);
        if (currentPlace.openingHours != null && currentPlace.openingHours.openNow != null && currentPlace.openingHours.openNow) lblOpenNow.setVisibility(View.VISIBLE);
        else lblOpenNow.setVisibility(View.GONE);
        String placeTypes = "Type of place: \n";
        for (int i = 0; i < currentPlace.types.length; i++)
        {
            String userFriendlyString = currentPlace.types[i];
            userFriendlyString = StringUtils.replace(userFriendlyString, "_", " ");
            placeTypes +=  StringUtils.capitalize(userFriendlyString);
            if (i+1 != currentPlace.types.length) placeTypes = placeTypes + ", ";
        }
        lblPlaceType.setText(placeTypes);
        lblRatingScore.setText(currentPlace.rating + " / 5");
        lblRatingNumberOfReviews.setText("From " + currentPlace.userRatingsTotal + " reviews");

        prepareNextPlace();
    }

    private void prepareNextPlace()
    {
        int nextPlacePosition = currentPlacePosition + 1;

        try {
            if ((nextPlacePosition) >= currentPlacesResponse.results.length)
            {
                loadPlaces(currentPlacesResponse.nextPageToken);
            }
            else
            {
                nextPlace = currentPlacesResponse.results[nextPlacePosition];
                if (currentPlacesResponse.results[nextPlacePosition].photos != null && currentPlacesResponse.results[nextPlacePosition].photos[0].photoReference != null)
                {
                    nextPlaceImage = PlacesApi.photo(geoApiContext, currentPlacesResponse.results[nextPlacePosition].photos[0].photoReference).maxHeight(1500).maxWidth(1500).await();
                }
                else
                {
                    nextPlaceImage = null;
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentPlacePosition = currentPlacePosition + 1;
    }

    private void createRoute()
    {
        CreateRouteRequest createRouteRequest = new CreateRouteRequest();
        createRouteRequest.RouteName = txtRouteName.getText().toString();
        createRouteRequest.Places = selectedPlace;

        try {
            routeCreateEndpointInvoker.PrepareCreateRoutePost(
                    TokenLifeManager.getInstance().getUserCredentialAccessToken(),
                    createRouteRequest,
                    new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            ShowToast(getApplicationContext(), "Create route failed.");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful())
                            {
                                ShowToast(getApplicationContext(), "Created Route");
                                onBackPressed(); //Why does this close the application?
                            }
                            else
                            {
                                ShowToast(getApplicationContext(), "Create route failed. API Threw error");
                            }
                        }
                    });
        } catch (RequiresAuthException e) {
            e.printStackTrace();
        }
    }
}
