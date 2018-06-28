package com.cync.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by zlinux on 06/10/16.
 */
public class CyncTank {

    public String id, user_id, title, description, type, date_added, ago, user_image, user_name, comment, like, attachment, thumbnail;
    public boolean islike;
    public ArrayList<Tags> tagList = new ArrayList<>();


    public CyncTank(String id, String user_id, String title, String description, String type, String date_added, String ago, String user_image, String user_name, String comment, String like, String attachment, boolean islike, String thumbnail, ArrayList<Tags> tagList) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.date_added = date_added;
        this.ago = ago;
        this.user_image = user_image;
        this.user_name = user_name;
        this.comment = comment;
        this.like = like;
        this.attachment = attachment;
        this.islike = islike;
        this.thumbnail = thumbnail;
        this.tagList = tagList;
    }

    public static String getStringFromObject(Context ct, CyncTank object) {

        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }


    public static CyncTank getObjectListFromString(Context ct, String mListStr) {

        CyncTank obj = null;

        Type type = new TypeToken<CyncTank>() {
        }.getType();
        obj = new Gson().fromJson(mListStr, type);

        return obj;
    }
}
