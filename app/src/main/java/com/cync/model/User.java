package com.cync.model;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The local JSON file users.json contains sample user data that is loaded and used
 *  to demonstrate '@' mentions.
 */
public class User {

    @SerializedName("friend_image")
    private String friend_image;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("id")
    private String uid;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public User(String firstName, String lastName, String uid,String friend_image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.friend_image = friend_image;
    }

    public String getFriend_image() {
        return friend_image;
    }

    public void setFriend_image(String friend_image) {
        this.friend_image = friend_image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFriendId() {
        return uid;
    }

    public void setFriendId(String uid) {
        this.uid = uid;
    }
}
