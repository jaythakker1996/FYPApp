package com.example.jaythakker.myapplication;

/**
 * Created by Kaushal on 15-01-2018.
 */

public class Restaurant {
    private String name;
    private String area;
    private String location;
    private int est_cost_per_person;

    public Restaurant(){

    }

    public void setName(String name){
        this.name = name;
    }
    public void setArea(String area){
        this.area = area;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public void setEst_cost_per_person(int cost){
        est_cost_per_person = cost;
    }

    public String getName(){
        return name;
    }
    public String getArea(){
        return area;
    }
    public String getLocation(){
        return location;
    }
    public int getEst_cost_per_person(){
        return est_cost_per_person;
    }

}
