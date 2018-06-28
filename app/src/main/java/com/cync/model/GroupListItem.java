package com.cync.model;

/**
 * Created by ketul.patel on 4/2/16.
 */
public class GroupListItem {

    public String group_id,group_name,group_image;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected;

    public GroupListItem(String group_id,String group_name,String group_image) {
        this.group_id=group_id;
        this.group_name=group_name;
        this.group_image=group_image;
    }


}
