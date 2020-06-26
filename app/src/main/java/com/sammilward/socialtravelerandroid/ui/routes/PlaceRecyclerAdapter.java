package com.sammilward.socialtravelerandroid.ui.routes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.Place;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.List;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Place> places;
    private final LayoutInflater layoutInflater;

    public PlaceRecyclerAdapter(Context context, List<Place> places)
    {
        this.context = context;
        this.places = places;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PlaceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_place_list, parent, false);
        return new ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull PlaceRecyclerAdapter.ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.currentPosition = position;
        holder.lblPosition.setText(String.valueOf(position+1));
        holder.lblName.setText(place.name);

        DecimalFormat df = new DecimalFormat("0.0");
        holder.lblRating.setText("Rating: " + df.format(place.rating));

        String types = "";
        for (int i = 0; i < place.types.size(); i++)
        {
            String userFriendlyString = place.types.get(i);
            userFriendlyString = StringUtils.replace(userFriendlyString, "_", " ");
            types = types + StringUtils.capitalize(userFriendlyString);
            if (i+1 != place.types.size()) types = types + ", ";
        }

        holder.lblNumberOfRatings.setText("Out of " + place.numberOfRatings + " reviews");
        holder.lblTypes.setText(types);

        byte[] imageData = Base64.decode(place.imageContent, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        holder.imgPlacePhoto.setImageBitmap(Bitmap.createScaledBitmap(bmp, 1500,1500, false));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgPlacePhoto;
        public TextView lblPosition, lblName, lblRating, lblNumberOfRatings, lblTypes;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlacePhoto = itemView.findViewById(R.id.imgPlacePhoto);
            lblPosition = itemView.findViewById(R.id.lblPosition);
            lblName = itemView.findViewById(R.id.lblName);
            lblRating = itemView.findViewById(R.id.lblRating);
            lblNumberOfRatings = itemView.findViewById(R.id.lblNumberOfRatings);
            lblTypes = itemView.findViewById(R.id.lblTypes);
        }
    }
}
