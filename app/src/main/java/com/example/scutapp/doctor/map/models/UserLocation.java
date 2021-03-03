package com.example.scutapp.doctor.map.models;

import com.example.scutapp.doctor.doctorchat.User;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {

    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;
    private User user;

    public UserLocation(GeoPoint geoPoint, Date timestamp, User user){
        this.geo_point = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UserLocation(){

    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geo_point=" + geo_point +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }
}
