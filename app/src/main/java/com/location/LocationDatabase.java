package com.location;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationDatabase {

    private static final String TAG = "LocationDatabase";

    public static SQLiteDatabase DatabaseLite = null;
    private final static String DATABASE = "LocationInfoDB";
    private static final int db_version = 1;
    Context con;
    DatabaseHelper dbhelp;
    static LocationDatabase instance;
    private final static String TABLE_LOCATION_HISTORY = "LocationHistory";

    private static final String CREATE_LOCATION_HISTORY = "create table LocationHistory(ID INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT," +
            " latitude TEXT," +
            " longitude TEXT," +
            " status TEXT," +
            " timestamp TEXT," +
            " speed REAL," +
            " avgspeed TEXT," +
            " distance TEXT," +
            " duration Text)";

    public LocationDatabase(Context con) {
        this.con = con;
        dbhelp = new DatabaseHelper(con);
    }

    public static LocationDatabase getInstance(Context c) // <-- added context as
    // parameter
    {
        synchronized (LocationDatabase.class) {
            if (instance == null) {
                instance = new LocationDatabase(c); // <-- used context in
                // constructor
            }
            return instance;
        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context con) {
            super(con, DATABASE, null, db_version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_LOCATION_HISTORY);

        }

        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {


            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_HISTORY);

            // Create tables again
            onCreate(db);

        }
    }

    public LocationDatabase open() throws SQLException {
        DatabaseLite = dbhelp.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelp.close();
    }

    public void UpdateLastRecord(String mduration, String mDistance) {

        int ID = -1;

        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory WHERE ID = (SELECT MAX(ID) FROM LocationHistory)", null);

        if (c.moveToFirst()) {
            do {
                ID = c.getInt(c.getColumnIndex("ID"));
            } while (c.moveToNext());
        }
        c.close();

        if (ID != -1) {
            ContentValues args = new ContentValues();
            args.put("duration", mduration);
            args.put("distance", mDistance);
            int x = DatabaseLite.update(TABLE_LOCATION_HISTORY, args, "ID=" + ID, null);
            Log.i(TAG, "UpdateLastRecord: Updated Distance = " + x);
        }
        close();
    }

    public void INSERT_NEW_MESSAGE(String userid, String latitude, String longitude, String status, String timestamp,
                                   float speed, String avgspeed, String distance, String duration) {
        open();

        ContentValues initialVal = new ContentValues();
        initialVal.put("userid", userid);
        initialVal.put("latitude", latitude);
        initialVal.put("longitude", longitude);
        initialVal.put("status", status);
        initialVal.put("timestamp", timestamp);
        initialVal.put("speed", speed);
        initialVal.put("avgspeed", avgspeed);
        initialVal.put("distance", distance);
        initialVal.put("duration", duration);
        DatabaseLite.insert(TABLE_LOCATION_HISTORY, null, initialVal);

        Log.d(TAG, "TABLE_LOCATION_HISTORY : Inserted = userid=" + userid + " lat->" + latitude + " lng->"
                + longitude + " status->" + status + " avgSpeed->" + avgspeed);

        close();
    }


    public String getAverageSpeed() {
        String avgSpeed = "";

        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory WHERE ID = (SELECT MAX(ID)  FROM LocationHistory)", null);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            avgSpeed = c.getString(c.getColumnIndex("avgspeed"));
            c.moveToNext();
        }

        Log.d("copunt", "getAverageSpeed = " + avgSpeed);
        c.close();

        close();
        return avgSpeed;
    }

    public String getDistance() {
        String distance = "";

        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory WHERE   ID = (SELECT MAX(ID)  FROM LocationHistory)", null);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {


            distance = c.getString(c.getColumnIndex("distance"));


            c.moveToNext();

        }

        Log.d("copunt", "getdistance =" + distance);
        c.close();

        close();
        return distance;
    }

    public String getDuration() {
        String duration = "";

        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory WHERE ID = (SELECT MAX(ID) FROM LocationHistory)", null);
        if (c.moveToFirst()) {
            do {
                duration = c.getString(c.getColumnIndex("duration"));
            } while (c.moveToNext());
        }
        Log.d(TAG, "getDuration =" + duration);
        c.close();

        close();
        return duration;
    }


    public String getTopSpeed() {
        String speed = "";

        open();
        Cursor c = DatabaseLite.rawQuery("select max(speed) as speed from LocationHistory", null);
        //Cursor c = DatabaseLite.query(TABLE_LOCATION_HISTORY, null, "speed=(SELECT MAX(speed))", null, null, null, null);

        Log.d("copunt", "getspeed =" + c.getCount());

        try {
            c.moveToFirst();
            speed = "" + c.getFloat(c.getColumnIndex("speed"));
        } finally {
            c.close();
        }

        Log.d("copunt", "getspeed =" + speed);
        c.close();

        close();
        return speed;
    }


    public void deleteAllData() {
        open();
        DatabaseLite.execSQL("delete from LocationHistory");
        close();
    }


//    public ArrayList<HashMap<String, String>> getAllData() {
//
//        String userid, latitude, longitude, status, timestamp,speed,avgspeed,distance,duration;
//
//        ArrayList<HashMap<String, String>> ImgList = new ArrayList<HashMap<String, String>>();
//        open();
//        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory", null);
//
//
//
//
//        Log.d("copunt", "-" + c.getCount());
//        c.moveToFirst();
//        for (int i = 0; i < c.getCount(); i++) {
//            HashMap<String, String> map = new HashMap<String, String>();
//            userid = c.getString(c.getColumnIndex("userid"));
//            latitude = c.getString(c.getColumnIndex("latitude"));
//            longitude = c.getString(c.getColumnIndex("longitude"));
//            status = c.getString(c.getColumnIndex("status"));
//            timestamp = c.getString(c.getColumnIndex("timestamp"));
//            speed = c.getString(c.getColumnIndex("speed"));
//            avgspeed = c.getString(c.getColumnIndex("avgspeed"));
//            distance = c.getString(c.getColumnIndex("distance"));
//            duration = c.getString(c.getColumnIndex("duration"));
//
//            map.put("userid", userid);
//            map.put("latitude", latitude);
//            map.put("longitude", longitude);
//            map.put("status", status);
//            map.put("timestamp", timestamp);
//            map.put("speed", speed);
//            map.put("avgspeed", avgspeed);
//            map.put("distance", distance);
//            map.put("duration", duration);
//            ImgList.add(map);
//            c.moveToNext();
//
//            Log.i(TAG, ":: userid = "+userid);
//            Log.i(TAG, ":: latitude = "+latitude);
//            Log.i(TAG, ":: longitude = "+longitude);
//            Log.i(TAG, ":: status = "+status);
//            Log.i(TAG, ":: timestamp = "+timestamp);
//            Log.i(TAG, ":: speed = "+speed);
//            Log.i(TAG, ":: avgspeed = "+avgspeed);
//            Log.i(TAG, ":: distance = "+distance);
//            Log.i(TAG, ":: duration = "+duration);
//        }
//
//        Log.d("copunt", "-" + c.getCount());
//
//        c.close();
//
//        close();
//        return ImgList;
//    }


    public String getAllDataJSON() {

        String userid, latitude, longitude, status, timestamp, speed, avgspeed, distance, duration;

        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory", null);

        JSONArray jMain = new JSONArray();

        Log.d("copunt", "-" + c.getCount());
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            JSONObject jObj = new JSONObject();
            HashMap<String, String> map = new HashMap<String, String>();
            userid = c.getString(c.getColumnIndex("userid"));
            latitude = c.getString(c.getColumnIndex("latitude"));
            longitude = c.getString(c.getColumnIndex("longitude"));
            status = c.getString(c.getColumnIndex("status"));
            timestamp = c.getString(c.getColumnIndex("timestamp"));
            speed = c.getString(c.getColumnIndex("speed"));
            avgspeed = c.getString(c.getColumnIndex("avgspeed"));
            distance = c.getString(c.getColumnIndex("distance"));
            duration = c.getString(c.getColumnIndex("duration"));

            try {
                jObj.put("lat", Double.parseDouble(latitude));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jObj.put("lon", Double.parseDouble(longitude));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jMain.put(jObj);

            c.moveToNext();

            Log.i(TAG, ":: userid = " + userid);
            Log.i(TAG, ":: latitude = " + latitude);
            Log.i(TAG, ":: longitude = " + longitude);
            Log.i(TAG, ":: status = " + status);
            Log.i(TAG, ":: timestamp = " + timestamp);
            Log.i(TAG, ":: speed = " + speed);
            Log.i(TAG, ":: avgspeed = " + avgspeed);
            Log.i(TAG, ":: distance = " + distance);
            Log.i(TAG, ":: duration = " + duration);
        }

        Log.d("copunt", "-" + c.getCount());
        c.close();

        close();
        return jMain.toString();
    }


    public List<LatLng> getLocationList() {

        String userid, latitude, longitude, status, timestamp, speed, avgspeed, distance, duration;

        ArrayList<HashMap<String, String>> ImgList = new ArrayList<HashMap<String, String>>();
        open();
        Cursor c = DatabaseLite.rawQuery("select * from LocationHistory", null);

        List<LatLng> mList = new ArrayList<>();


        Log.d("copunt", "-" + c.getCount());
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {

            double lat = 0.0, lng = 0.0;

            JSONObject jObj = new JSONObject();
            HashMap<String, String> map = new HashMap<String, String>();
            userid = c.getString(c.getColumnIndex("userid"));
            latitude = c.getString(c.getColumnIndex("latitude"));
            longitude = c.getString(c.getColumnIndex("longitude"));
            status = c.getString(c.getColumnIndex("status"));
            timestamp = c.getString(c.getColumnIndex("timestamp"));
            speed = c.getString(c.getColumnIndex("speed"));
            avgspeed = c.getString(c.getColumnIndex("avgspeed"));
            distance = c.getString(c.getColumnIndex("distance"));
            duration = c.getString(c.getColumnIndex("duration"));


            try {
                lat = Double.parseDouble(latitude);
                lng = Double.parseDouble(longitude);

                mList.add(new LatLng(lat, lng));
            } catch (NumberFormatException e) {

            }


//            map.put("userid", userid);
//            map.put("latitude", latitude);
//            map.put("longitude", longitude);
//            map.put("status", status);
//            map.put("timestamp", timestamp);
//            map.put("speed", speed);
//            map.put("avgspeed", avgspeed);
//            map.put("distance", distance);
//            map.put("duration", duration);
//            ImgList.add(map);
            c.moveToNext();


        }

        Log.d("copunt", "-" + c.getCount());

        c.close();

        close();
        return mList;
    }

    public void deleteRecord(String uniId) {
        open();
        DatabaseLite.execSQL("delete from LocationHistory where uniqueid='" + uniId + "'");
        close();
    }

    public void updateEntry(String userid, String latitude, String avgspeed) {

        open();
        ContentValues args = new ContentValues();
        args.put("speed", "true");

        DatabaseLite.update(TABLE_LOCATION_HISTORY, args,
                "userid='" + userid + "' AND latitude='" + latitude + "'", null);

        close();
    }


}