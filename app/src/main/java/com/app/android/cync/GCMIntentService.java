package com.app.android.cync;

/**
 * Created by ketul.patel on 5/2/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ApplicationController;
import com.cync.model.UserDetail;
import com.google.android.gms.gcm.GcmListenerService;
import com.utils.CommonClass;

import java.util.Calendar;

import me.leolin.shortcutbadger.ShortcutBadger;


public class GCMIntentService extends GcmListenerService {
//
//    final String TRUE = "1";
    String senderId = "",notification_id="";
    boolean is_groupInfo=false;
    String type="";
    @Override
    public void onMessageReceived(String from, Bundle intent) {


        Log.e("Cync---", "onMessageReceived"+intent);
//        {"multicast_id":5557541224595005150,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1454735957116049%281ae844f9fd7ecd"}]}{"status":true,"message":"Friend request sent successfully.",
//                "data":[{"user_id":"32","user_name":"","user_image":"1454660023.png","first_name":"","last_name":"","email":"sachin@gmail.com","latitude":"23.01203333333334","longitude":"72.51075333333333","status":"pending"}]}
        if (!CommonClass.getUserpreference(this).user_id.equalsIgnoreCase("")) {
            is_groupInfo=false;
            try {
                String msg = intent.getString("message");
                senderId= intent.getString("sender_id","");


                if(intent.containsKey("ty+pe")) {
                    type = intent.getString("ty+pe");
                    Log.e("push recived Type==", "push recived Type==" + type);
                    Log.e("push recived Type==", "push recived Type==" + senderId);
                }

                if(intent.containsKey("notification_id")) {
                    notification_id = intent.getString("notification_id");

                }


//                Log.e("push recived Message= ", msg);
                int index = 0;

                if(type.equalsIgnoreCase("silent_push"))
                {
                    String  countstr=intent.getString("message");

                            try{
                                int count=Integer.parseInt(countstr);
                                ShortcutBadger.applyCount(ApplicationController.getContext(), count);
                            }catch (NumberFormatException e)
                            {

                            }

                }

                else if(type.equalsIgnoreCase("new_post"))
                {


                    prepareNotificationLikeComment(getApplicationContext(), "Cync", intent.getString("message"),senderId,type,notification_id);

                }
                else if(type.equalsIgnoreCase("like") || type.equalsIgnoreCase("unlike") || type.equalsIgnoreCase("comment") )
                {


                    prepareNotificationLikeComment(getApplicationContext(), "Cync", intent.getString("message"),senderId,type,notification_id);
                }

                else   if (msg.contains("accepted")) {

                    if (msg.contains("friend")) {
                        index = 2;
                    } else if (msg.contains("group")) {
                        index = 3;

                    }
                    prepareNotification(getApplicationContext(), "Cync", intent.getString("message"), index, intent);

                } else if(msg.contains("location")){
                    index=3;

                    is_groupInfo=true;
                    prepareNotification(getApplicationContext(), "Cync", intent.getString("message"), index, intent);

                }else{
                    index=4;
                    prepareNotification(getApplicationContext(), "Cync", intent.getString("message"), index, intent);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }







    private void prepareNotificationLikeComment(Context context, String title, String msg, String post_id,String type,String notification_id) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        long when = System.currentTimeMillis();
        Notification notification;
        /*notification = new Notification(R.drawable.ic_launcher, msg, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;*/
        /**
         * 1- Friend Request
         * 2- Friend request accept
         * 3- Group Invite Request
         * 4- Group Invitation Accept
         */
        UserDetail userDetail = CommonClass.getUserpreference(context);
        Intent intent;

        if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null")) {
            intent = new Intent(context, CyncTankDetailsActiivty.class);

            intent.putExtra("post_id", post_id);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("type", type);
            intent.setPackage(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
//        msg=CommonClass.strEncodeDecode(msg.trim(),true);
//        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
//                R.mipmap.ic_launcher);
        builder.setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(msg)
//                .setTicker(msg)
                .setStyle(new Notification.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent);
        notification = builder.getNotification();
        notification.contentView.setImageViewResource(android.R.id.icon,R.mipmap.ic_launcher);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
//
//            if (smallIconViewId != 0) {
//                if (notification.contentIntent != null)
//                    notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//
//                if (notification.headsUpContentView != null)
//                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//
//                if (notification.bigContentView != null)
//                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//            }
//        }
        //----
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        notification.sound = alarmSound;
        notification.defaults |= Notification.DEFAULT_SOUND;
        //-------
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE|Notification.FLAG_AUTO_CANCEL;
        manager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), notification);
    }

    private void prepareNotification(Context context, String title, String msg, int type, Bundle intent_bundle) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        long when = System.currentTimeMillis();
        Notification notification;
        /*notification = new Notification(R.drawable.ic_launcher, msg, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;*/
        /**
         * 1- Friend Request
         * 2- Friend request accept
         * 3- Group Invite Request
         * 4- Group Invitation Accept
         */
        UserDetail userDetail = CommonClass.getUserpreference(context);
        Intent intent;

        if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null")) {
            intent = new Intent(context, NavigationDrawerActivity.class);
            switch (type) {
                case 2:
                    intent.putExtra("notification", "5");
                    break;
                case 3:
                    intent.putExtra("notification", "7");
                    if(is_groupInfo)
                        intent.putExtra("group_id",senderId );
                    break;
                case 4:
                    intent.putExtra("notification", "8");
                    break;
            }
            intent.setPackage(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//            Log.e("type--------", "" + type);
//            intent.setPackage(context.getPackageName());
//            Log.e("MESSAGE________", "" + msg + " " + msg.contains("accepted"));
//            if (msg.contains("accepted")) {
//                Log.e("contains IF--------", "" + msg + " " + msg.contains("friend"));
//                if (msg.contains("friend")) {
//                    Log.e("friend--------", "" + msg);
////                    CommonClass.setPrefranceByKey_Value(context,"Notification","notify","2");
//                    intent.putExtra("notification", "2");
//                } else if (msg.contains("group")) {
//                    Log.e("group--------", "" + msg);
////                    CommonClass.setPrefranceByKey_Value(context,"Notification","notify","3");
//                    intent.putExtra("notification", "3");
//                }
//            } else {
//                Log.e("contains else--------", "" + msg);
////                CommonClass.setPrefranceByKey_Value(context,"Notification","notify","4");
//                intent.putExtra("notification", "4");
//            }
        } else {
            intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
//        msg=CommonClass.strEncodeDecode(msg.trim(),true);
//        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
//                R.mipmap.ic_launcher);
        builder.setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(msg)
//                .setTicker(msg)
                .setStyle(new Notification.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent);
        notification = builder.getNotification();
        notification.contentView.setImageViewResource(android.R.id.icon,R.mipmap.ic_launcher);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
//
//            if (smallIconViewId != 0) {
//                if (notification.contentIntent != null)
//                    notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//
//                if (notification.headsUpContentView != null)
//                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//
//                if (notification.bigContentView != null)
//                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
//            }
//        }
        //----
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        notification.sound = alarmSound;
        notification.defaults |= Notification.DEFAULT_SOUND;
        //-------
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE|Notification.FLAG_AUTO_CANCEL;
        manager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), notification);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }
//            if(type.equalsIgnoreCase("1")||type.equalsIgnoreCase("3")) {
////                bnd.putString("notification", "4");
//                Log.e(" IF--------",""+msg+" Request ");
//                intent.putExtra("notification", "4");
//            }
//            else if(type.equalsIgnoreCase("2")){
//                Log.e(" else IF--------",""+msg+" Friend ");
////                bnd.putString("notification", "2");
//                intent.putExtra("notification", "2");
//            }else if(type.equalsIgnoreCase("4")){
//                Log.e("else IF--------",""+msg+" Group ");
////                bnd.putString("notification", "3");
//                intent.putExtra("notification", "3");
//            }
//
//
}