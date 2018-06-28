package com.cync.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by zlinux on 06/10/16.
 */
public class Comments {



    public String  id,post_id,user_id,type,text,date_added,user_name,user_image,ago,post_by;
    public ArrayList<Tags> tagList = new ArrayList<>();

    public Comments(String id, String post_id, String user_id, String type, String text, String date_added, String user_name, String user_image, String ago,String post_by,ArrayList<Tags> tagList) {
        this.id = id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.type = type;
        this.text = text;
        this.date_added = date_added;
        this.user_name = user_name;
        this.user_image = user_image;
        this.ago = ago;
        this.post_by=post_by;
        this.tagList = tagList;
    }

    public static String getStringFromObject(Context ct, Comments object) {

        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }


    public static Comments getObjectListFromString(Context ct, String mListStr) {

        Comments obj = null;

        Type type = new TypeToken<Comments>() {
        }.getType();
        obj = new Gson().fromJson(mListStr, type);

        return obj;
    }
}
