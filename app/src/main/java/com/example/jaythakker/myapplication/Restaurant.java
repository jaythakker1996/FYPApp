package com.example.jaythakker.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kaushal on 15-01-2018.
 */

public class Restaurant implements Parcelable{
    private String restId;
    private String name;
    private String area;
    private double latitude;
    private double longitude;
    private String cuisine;
    private double est_cost_per_person;


    public Restaurant(String restId, String name, String area, double latitude, double longitude, String cuisine, double est_cost_per_person){
        this.restId = restId;
        this.name = name;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cuisine = cuisine;
        this.est_cost_per_person = est_cost_per_person;
    }

    public Restaurant(Parcel in) {
        restId = in.readString();
        name = in.readString();
        area = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        cuisine = in.readString();
        est_cost_per_person = in.readDouble();
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setArea(String area){
        this.area = area;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCuisine(String cuisine){
        this.cuisine = cuisine;
    }

    public void setEst_cost_per_person(double cost){
        est_cost_per_person = cost;
    }

    public String getRestId() {
        return restId;
    }

    public String getName(){
        return name;
    }

    public String getArea(){
        return area;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getCuisine() {
        return cuisine;
    }

    public double getEst_cost_per_person(){
        return est_cost_per_person;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(restId);
        parcel.writeString(name);
        parcel.writeString(area);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(cuisine);
        parcel.writeDouble(est_cost_per_person);
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>(){
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
