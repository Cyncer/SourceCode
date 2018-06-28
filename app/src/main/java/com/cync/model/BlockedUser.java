package com.cync.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by zlinux on 06/10/16.
 */
public class BlockedUser {



    public String user_id,user_name,user_image;


    public BlockedUser(String user_id, String user_name, String user_image) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_image = user_image;
    }

    public static String getStringFromObject(Context ct, BlockedUser object) {

        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }


    public static BlockedUser getObjectListFromString(Context ct, String mListStr) {

        BlockedUser obj = null;

        Type type = new TypeToken<BlockedUser>() {
        }.getType();
        obj = new Gson().fromJson(mListStr, type);

        return obj;
    }
}
