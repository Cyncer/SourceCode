package com.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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

    private static final String NOTIFICATION_CHANNEL_ID = "1235";

    private String senderId = "";
    private String notification_id = "";
    private boolean is_groupInfo = false;
    private String type = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "data->" + remoteMessage.getData());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(MyFirebaseMessengingService.this, "You have a notification", Toast.LENGTH_SHORT).show();
            }
        });

        createChannel();

        setupNotification(remoteMessage.getData());
    }


    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
                    return;
                }
            }

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Default",
                    importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (alarmSound == null) {
                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
            }
            if (alarmSound != null)
                notificationChannel.setSound(alarmSound, null);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
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
                if (msg == null)
                    msg = "";

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

        long when = System.currentTimeMillis();
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
        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context);
        notifiBuilder.setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setWhen(when)
                .setShowWhen(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifiBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Calendar.getInstance().get(Calendar.MILLISECOND)/*ID of notification*/, notifiBuilder.build());
        }
    }

    private void prepareNotification(Context context, String title, String msg, int type) {

        long when = System.currentTimeMillis();
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
        } else {
            intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context);
        notifiBuilder.setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setWhen(when)
                .setShowWhen(true)
                .setSmallIcon(getNotificationIcon())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifiBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Calendar.getInstance().get(Calendar.MILLISECOND)/*ID of notification*/, notifiBuilder.build());
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }

}
