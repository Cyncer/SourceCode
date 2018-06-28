package com.cync.model;

/**
 * Created by ketul.patel on 8/2/16.
 */
public class Notification {

    public String read;
    public String notification_id;
    public String notification_name;
    public String notification_type;
    public String notification_image;
    public String notification_status;
    public String group_admin_id_;
    public String group_admin_name;
    public String group_admin_image;
    public String notification_time;
    public String request_id;
    public String created_at;






    public Notification(String notification_id, String notification_name, String notification_type, String notification_image, String notification_status, String notification_time, String group_admin_id_, String group_admin_name,String request_id,String created_at) {
        this.notification_id = notification_id;
        this.notification_name = notification_name;
        this.notification_type = notification_type;
        this.notification_image = notification_image;
        this.notification_status = notification_status;
        this.group_admin_id_ = group_admin_id_;
        this.group_admin_name = group_admin_name;
        this.request_id=request_id;
        this.notification_time=notification_time;
        this.created_at=created_at;
    }

    public Notification(String notification_id, String notification_name, String notification_type, String notification_image, String notification_status, String notification_time,String request_id,String created_at) {
        this.notification_id = notification_id;
        this.notification_name = notification_name;
        this.notification_type = notification_type;
        this.notification_image = notification_image;
        this.notification_status = notification_status;
        this.notification_time = notification_time;
        this.request_id=request_id;
        this.created_at=created_at;
    }
}
