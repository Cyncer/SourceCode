package com.cync.model;

import java.io.Serializable;

/**
 * Created by milind.shete on 21-01-2016.
 */
public class FriendListDetail implements Serializable {

    private boolean isSelected;
    private String friendIcon;
    private String friendName;
    private String deleteIcon;

    public FriendListDetail(String friendIcon, String friendName, boolean isSelected){
        this.friendIcon = friendIcon;
        this.friendName = friendName;
        this.isSelected = isSelected;
    }

    public FriendListDetail(String friendIcon, String friendName, String deleteIcon) {
        this.friendIcon = friendIcon;
        this.friendName = friendName;
        this.deleteIcon = deleteIcon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getDeleteIcon() {
        return deleteIcon;
    }

    public void setDeleteIcon(String deleteIcon) {
        this.deleteIcon = deleteIcon;
    }
}
