package com.cync.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by ketul.patel on 21/1/16.
 */
public class UserDetail implements Serializable {

    public String user_id;
    public String user_image;
    public String first_name;
    public String last_name;
    public String login_type;
    public String email;
    public String share_location;
    public String lat;
    public String lng;
    public Double dLat;
    public Double dLng;
    public String user_type;
    public boolean notification;
    public boolean group_location;
    public boolean group_is_friend;
    public boolean view_my_rides=true;
    public boolean hide_top_speed=false;
    public String satus_friend_request;
    public String status;
    public String update_location;
    public String make="";

    public String model="";
    public String year="";


    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public boolean getGroupLocation() {

        return group_location;
    }

    public void setGroupLocation(boolean group_location) {
        this.group_location = group_location;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public UserDetail(String user_id, String user_image, String first_name, String last_name, String login_type, String email, boolean notification

    ,String make,String model,String year,boolean view_my_rides,
                               boolean hide_top_speed) {
        this.user_id = user_id;
        this.user_image = user_image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.login_type = login_type;
        this.notification = notification;
        this.email = email;
        this.make=make;
        this.model=model;
        this.year=year;

         this.view_my_rides=view_my_rides;
       this.hide_top_speed=hide_top_speed;
    }

    public UserDetail(String user_id, String user_image, String first_name, String last_name, String login_type, String email, boolean notification,boolean  view_my_rides,boolean hide_top_speed) {
        this.user_id = user_id;
        this.user_image = user_image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.login_type = login_type;
        this.notification = notification;
        this.email = email;
        this.view_my_rides=view_my_rides;
        this.hide_top_speed=hide_top_speed;

    }

    public UserDetail(String user_id, String user_image, String first_name, String last_name, String login_type, String email) {
        this.user_id = user_id;
        this.user_image = user_image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.login_type = login_type;
        this.email = email;
    }

    public UserDetail(String user_id, String first_name, String user_image, String email, String status) {
        this.user_id = user_id;
        this.user_image = user_image;
        this.first_name = first_name;
        this.status = status;
        this.email = email;
    }

    public UserDetail(String user_id, String user_image, String first_name, String last_name, String share_location, String user_type, String lat, String lng, boolean group_location, boolean group_is_friend, String satus_friend_request) {
        this.user_id = user_id;
        this.user_image = user_image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.share_location = share_location;
        this.user_type = user_type;
        this.lat = lat;
        this.lng = lng;
        this.isSelected = group_location;
        this.group_is_friend = group_is_friend;
        this.satus_friend_request = satus_friend_request;
    }

    public UserDetail(String user_id, String first_name, LatLng latLng, String user_image,boolean isFriend,String update_location) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.dLat = latLng.latitude;
        this.dLng = latLng.longitude;
        this.user_image = user_image;
        this.group_is_friend=isFriend;
        this.update_location=update_location;
    }
}