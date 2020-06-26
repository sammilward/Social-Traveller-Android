package com.sammilward.socialtravelerandroid.ui.routes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sammilward.socialtravelerandroid.R;
import com.sammilward.socialtravelerandroid.http.routeManagement.GetAllRoute;

import java.util.List;

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<GetAllRoute> routes;
    private final LayoutInflater layoutInflater;

    public RouteRecyclerAdapter(Context context, List<GetAllRoute> routes)
    {
        this.context = context;
        this.routes = routes;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RouteRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_route_list, parent, false);
        return new ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull RouteRecyclerAdapter.ViewHolder holder, int position) {
        GetAllRoute route = routes.get(position);
        holder.routeId = route.id;
        holder.routeCreatorId = route.creatorId;
        holder.lblName.setText(route.name);
        holder.lblCity.setText(route.city);
        holder.lblNumberPlacesOnRoute.setText("Number of places: " + route.places.size());
        holder.lblRating.setText("Rating: " + route.rating);
        holder.lblCreator.setText("Creator: " + route.creatorUsername);

        String placeNames = "";
        for (int i = 0; i < route.places.size(); i++)
        {
            placeNames = placeNames + route.places.get(i).name;
            if (i+1 != route.places.size()) placeNames = placeNames + ", ";
        }

        holder.lblPlacesOnRoute.setText(placeNames);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblName, lblCity, lblNumberPlacesOnRoute, lblPlacesOnRoute, lblRating, lblCreator;
        public String routeId, routeCreatorId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblName = itemView.findViewById(R.id.lblName);
            lblCity = itemView.findViewById(R.id.lblCity);
            lblNumberPlacesOnRoute = itemView.findViewById(R.id.lblNumberPlacesOnRoute);
            lblPlacesOnRoute = itemView.findViewById(R.id.lblPlacesOnRoute);
            lblRating = itemView.findViewById(R.id.lblRating);
            lblCreator = itemView.findViewById(R.id.lblCreator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RouteDetailsActivity.class);
                    intent.putExtra("routeId", routeId);
                    intent.putExtra("routeCreatorId", routeCreatorId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
