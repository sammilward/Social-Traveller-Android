package com.sammilward.socialtravelerandroid.http.userManagement;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class User implements Parcelable {
    public String id;
    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public String birthCountry;
    public double latitude;
    public double longitude;
    public String currentCountry;
    public String currentCity;
    public String dob;
    public boolean male;

    protected User(Parcel in) {
        id = in.readString();
        username = in.readString();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        birthCountry = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        currentCountry = in.readString();
        currentCity = in.readString();
        dob = in.readString();
        male = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getDOBAsString(String dateFormat)
    {
        SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = toDateFormat.parse(dob);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    public LocalDate getDOBAsLocalDate()
    {
        String dateString = dob.substring(0, dob.indexOf('T'));
        return LocalDate.parse(dateString);
    }

    public int getAge()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate Dob = LocalDate.parse(dob.substring(0,10), formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(Dob, currentDate).getYears();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(birthCountry);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(currentCountry);
        parcel.writeString(currentCity);
        parcel.writeString(dob);
        parcel.writeByte((byte) (male ? 1 : 0));
    }
}
