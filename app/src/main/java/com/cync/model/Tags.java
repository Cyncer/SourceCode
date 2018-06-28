package com.cync.model;

/**
 * Created by zlinux on 16/6/17.
 */

public class Tags {

    public String id,name;
    public int startIndex,length;

    public Tags(String id, String name, int startIndex, int length) {
        this.id = id;
        this.name = name;
        this.startIndex = startIndex;
        this.length = length;
    }
}
