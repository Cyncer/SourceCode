package com.cync.model;

/**
 * Created by ketul.patel on 20/1/16.
 */
public class FriendDetail {
//    {"user_id":"7","user_name":"","user_image":"1454397632.jpg","first_name":"test","last_name":"temo",
//            "email":"vijay@gmail.com","latitude":"7844374","longitude":"78655784","status":"accept"}
public boolean isSelected=false, requestGroup;
    public String friendImage,requestFriend;
    public String friendName;
    public String friendId;
    public String friendEmailId;
    public String friendLat;
    public String friendLng;
    public String requestTime;
    public String message;
    public String type;

    public FriendDetail(String friendName) {
        this.friendName = friendName;
    }



    public FriendDetail(boolean checked, boolean requestGroup, String requestFriend, String friendImage, String friendName, String friendId, String friendEmailId, String lat, String lng) {
        this.requestGroup = requestGroup;
        this.requestFriend = requestFriend;
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendId = friendId;
        this.friendEmailId = friendEmailId;
        this.friendLat = lat;
        this.friendLng = lng;
    }

    public FriendDetail(String friendId,String friendName,String friendImage,String friendEmailId,String friendLat,String friendLng,String requestFriend) {
        this.requestFriend = requestFriend;
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendId = friendId;
        this.friendEmailId = friendEmailId;
        this.friendLat = friendLat;
        this.friendLng = friendLng;
    }

    public FriendDetail(String friendId,String friendName,String friendImage,String requestFriend) {
        this.requestFriend = requestFriend;
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendId = friendId;
    }
    public FriendDetail(String friendId,String friendName,String friendImage,String message,String requestTime) {
        this.friendImage = friendImage;
        this.friendName = friendName;
        this.friendId = friendId;
        this.requestTime=requestTime;
        this.message=message;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }


}
