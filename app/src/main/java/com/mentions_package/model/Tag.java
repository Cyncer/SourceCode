package com.mentions_package.model;

/**
 * Created by zlinux on 19/7/17.
 */

public class Tag {

    public String id,name;
    public int startIndex,length;

    public Tag(String id, String name, int startIndex, int length) {
        this.id = id;
        this.name = name;
        this.startIndex = startIndex;
        this.length = length;
    }
}
