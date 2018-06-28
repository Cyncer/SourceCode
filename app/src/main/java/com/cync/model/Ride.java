package com.cync.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by zlinux on 10/11/17.
 */

public class Ride {

    public  String id,user_id,ride_name,ride_type,ride_difficulty,distance,duration,top_speed,avg_speed,date_added,
            share_ride,polyline_id,color,status;

    public int colorCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<LatLng> mDataList=new ArrayList<>();

    public Ride(String id, String user_id, String ride_name, String ride_type, String ride_difficulty, String distance, String duration, String top_speed, String avg_speed, String date_added, String share_ride,String color,int colorCode, ArrayList<LatLng> mDataList) {
        this.id = id;
        this.user_id = user_id;
        this.ride_name = ride_name;
        this.ride_type = ride_type;
        this.ride_difficulty = ride_difficulty;
        this.distance = distance;
        this.duration = duration;
        this.top_speed = top_speed;
        this.avg_speed = avg_speed;
        this.date_added = date_added;
        this.share_ride = share_ride;
        this.color = color;
        this.colorCode = colorCode;
        this.mDataList = mDataList;
    }



    public String getPolyline_id() {
        return polyline_id;
    }

    public void setPolyline_id(String polyline_id) {
        this.polyline_id = polyline_id;
    }
}
