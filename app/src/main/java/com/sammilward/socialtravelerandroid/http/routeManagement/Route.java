package com.sammilward.socialtravelerandroid.http.routeManagement;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Route implements Parcelable {
    public String id;
    public String name;
    private String creatorId;
    private String creatorUsername;
    private String city;
    private String country;
    public int rating;
    public List<Place> places;
    public double[] routeCoords;
    public boolean userLikes;


    protected Route(Parcel in) {
        id = in.readString();
        name = in.readString();
        creatorId = in.readString();
        creatorUsername = in.readString();
        city = in.readString();
        country = in.readString();
        rating = in.readInt();
        places = in.createTypedArrayList(Place.CREATOR);
        routeCoords = in.createDoubleArray();
        userLikes = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(creatorId);
        dest.writeString(creatorUsername);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeInt(rating);
        dest.writeTypedList(places);
        dest.writeDoubleArray(routeCoords);
        dest.writeByte((byte) (userLikes ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
