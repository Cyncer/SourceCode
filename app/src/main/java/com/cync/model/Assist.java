package com.cync.model;

/**
 * Created by zlinux on 6/9/16.
 */
public class Assist {

    public String id,name,address,contact_number,lat,lng,make_name,year,model;

    public Assist(String id, String name, String address, String contact_number, String lat, String lng, String make_name, String year, String model) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact_number = contact_number;
        this.lat = lat;
        this.lng = lng;
        this.make_name = make_name;
        this.year = year;
        this.model = model;
    }
}
