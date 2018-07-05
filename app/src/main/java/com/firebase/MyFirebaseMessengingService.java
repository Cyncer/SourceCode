package com.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.LoginActivity;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.utils.CommonClass;

import java.util.Calendar;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessengingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessengingService.class.getSimpleName();


    private String senderId = "";
    private String notification_id = "";
    private boolean is_groupInfo = false;
    private String type = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "data->" + remoteMessage.getData());
        setupNotification(remoteMessage.getData());
    }

    /*{
            "status":true,
            "message":"Friend request sent successfully.",
            "data":[
        {
                "user_id":"32",
                "user_name":"",
                "user_image":"1454660023.png",
                "first_name":"",
                "last_name":"",
                "email":"sachin@gmail.com",
                "latitude":"23.01203333333334",
                "longitude":"72.51075333333333",
                "status":"pending"
        }
                ]
    }*/

    private void setupNotification(Map<String, String> data) {

        if (!CommonClass.getUserpreference(this).user_id.equalsIgnoreCase("")) {
            is_groupInfo = false;
            try {
                String msg = data.get("message");
                senderId = data.get("sender_id");

                if (senderId == null)
                    senderId = "";

                if (data.containsKey("ty+pe")) {
                    type = data.get("ty+pe");
                    Log.e("push recived Type==", "push recived Type==" + type);
                    Log.e("push recived Type==", "push recived Type==" + senderId);
                }

                if (data.containsKey("notification_id")) {
                    notification_id = data.get("notification_id");
                }

                int index = 0;

                if (type.equalsIgnoreCase("silent_push")) {
                    String countstr = data.get("message");
                    try {
                        int count = Integer.parseInt(countstr);
                        ShortcutBadger.applyCount(ApplicationController.getContext(), count);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (type.equalsIgnoreCase("new_post")) {
                    prepareNotificationLikeComment(getApplicationContext(), "Cync", data.get("message"), senderId, type, notification_id);
                } else if (type.equalsIgnoreCase("like") || type.equalsIgnoreCase("unlike") || type.equalsIgnoreCase("comment")) {
                    prepareNotificationLikeComment(getApplicationContext(), "Cync", data.get("message"), senderId, type, notification_id);
                } else if (msg.contains("accepted")) {
                    if (msg.contains("friend")) {
                        index = 2;
                    } else if (msg.contains("group")) {
                        index = 3;

                    }
                    prepareNotification(getApplicationContext(), "Cync", data.get("message"), index);

                } else if (msg.contains("location")) {
                    index = 3;

                    is_groupInfo = true;
                    prepareNotification(getApplicationContext(), "Cync", data.get("message"), index);

                } else {
                    index = 4;
                    prepareNotification(getApplicationContext(), "Cync", data.get("message"), index);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Push notification methods (copied from previous one)

    private void prepareNotificationLikeComment(Context context, String title, String msg,
                                                String post_id, String type, String notification_id) {
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

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
        notification.contentView.setImageViewResource(android.R.id.icon, R.mipmap.ic_launcher);

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
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        notification.sound = alarmSound;
        notification.defaults |= Notification.DEFAULT_SOUND;
        //-------
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
        assert manager != null;
        manager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), notification);
    }

    private void prepareNotification(Context context, String title, String msg, int type) {
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
                    if (is_groupInfo)
                        intent.putExtra("group_id", senderId);
                    break;
                case 4:
                    intent.putExtra("notification", "8");
                    break;
            }
            intent.setPackage(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        notification.contentView.setImageViewResource(android.R.id.icon, R.mipmap.ic_launcher);

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
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        notification.sound = alarmSound;
        notification.defaults |= Notification.DEFAULT_SOUND;
        //-------
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
        if (manager != null) {
            manager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), notification);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }

}
