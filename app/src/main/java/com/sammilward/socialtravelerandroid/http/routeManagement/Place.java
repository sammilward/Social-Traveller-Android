package com.sammilward.socialtravelerandroid.http.routeManagement;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Place implements Parcelable {
    public String placeId;
    public String name;
    public String photoReference;
    public double latitude;
    public double longitude;
    public double rating;
    public int numberOfRatings;
    public List<String> types;
    public String imageContent;

    public Place() {}

    protected Place(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        photoReference = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        rating = in.readDouble();
        numberOfRatings = in.readInt();
        types = in.createStringArrayList();
        imageContent = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
        parcel.writeString(name);
        parcel.writeString(photoReference);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(rating);
        parcel.writeInt(numberOfRatings);
        parcel.writeStringList(types);
        parcel.writeString(imageContent);
    }
}
