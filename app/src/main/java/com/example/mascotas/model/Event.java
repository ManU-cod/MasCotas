package com.example.mascotas.model;

import java.util.Date;

public class Event {
    public String title;
    public String information;
    public String cost;
    public Double longitude;
    public Double latitude;
    public Date date;

    public Event() {

    }

// Constructor, getters y setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getInformation() {
        return information;
    }

    public String getCost() {
        return cost;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Date getDate() {
        return date;
    }


}