package com.xmpp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class XMPPService extends Service {
    public static final String TAG = "XMPPService";
    private static SmackAndroid asmk = null;

    public static XMPP openFireConnection;

    SharedPreferences sharedPref;

    int notificationID = 111;

    String username;
    String password;
    String serveraddress;
    static String domain;
    int serverport = 9000;
    // Schedule job parameters
    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);
    static ScheduledFuture senderHandle;

    static NotificationManager mNotificationManager;

    // Service Methods
    // ************************************************************
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        // super.onCreate();

        // Required for using aSmack - also add the DNSjava jar fire in library
        asmk = SmackAndroid.init(XMPPService.this);


        // run service on seperate thread
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        getSharedPreference();


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request

                openFireConnection = new XMPP(serveraddress, username, password, domain);
                connectCall();
            }
        });
        thread1.start();


//		if (connectCall())
//        {
//
////			Toast.makeText(getApplicationContext(),
////					"you are connected to server", Toast.LENGTH_LONG).show();
//			// Notification
//			//createNotificationIcon();
//			// Schedule function call
//			// sendForAnHour();
//		}
//		else
        //connectCall();
//			Toast.makeText(getApplicationContext(),
//					"you are not connected to chat server", Toast.LENGTH_LONG)
//					.show();

    }




    int no=0;
    public boolean connectCall() {




        ConnectionDetector cd = new ConnectionDetector(ApplicationController.getContext());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected ) {
            if (openFireConnection.connect()) {
                return openFireConnection.connect();
            } else {

                if(no<300) {
                    no++;
                    return connectCall();
                }
                else
                    return false;
            }
        }
        else
        {
            Log.i(TAG, "connectCall: No Internet Connection");

            return false;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
//		Toast.makeText(this, "OpenFire Service Started", Toast.LENGTH_LONG)
//				.show();
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Disconnecting from Server
        try {
            System.out.println("************SERVICE DESTROYED *********");
            UserDetail userDetail = CommonClass.getUserpreference(ApplicationController.getContext());
            if (userDetail == null) {
                disconnectFromOpenFireServer();
            }
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Disconnection Error", e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Deleting all notifications
        try {
            cancellAllNotifications();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Notification Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Cancel periodic job
        // sendForAnHourCancel();

//		Toast.makeText(this, "OpenFire Service Destroyed", Toast.LENGTH_LONG)
//				.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    // *************************************************************************
    // *************************************************************************
    private void getSharedPreference() {
        // TODO Auto-generated method stub

        username = CommonClass.getChatUsername(getApplicationContext());
        password = CommonClass.getChatUsername(getApplicationContext());
        serveraddress = Constants.HOST;//sharedPref.getString("textServerAddress", "");
        domain = Constants.domain;
        Log.d("Settings in Service", "Settings in Service=" + username + "," + password + ","
                + serveraddress + ":" + serverport + "," + domain);
    }

    private void createNotificationIcon() {
        // TODO Auto-generated method stub
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("XMPP Service")
                .setContentText(
                        "this service is for keep connecting to openfire server!");
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.getNotification());

    }

    // function to send message to addressed user
    public static void sendMessage(String addressedUser2, String sendmsg, String type)

            throws NotConnectedException {

        try {
            // TODO Auto-generated method stub

            System.out.println("ChatActivity.sendMessage===============" + type);
            if (openFireConnection != null && addressedUser2 != null && sendmsg != null)
                openFireConnection.chat(addressedUser2, sendmsg, type);
        } catch (NullPointerException e) {

        }
    }


    public static XMPPConnection getXmppCon() {
        return openFireConnection.getCommnection();
    }

    // function to Disconnect from openFire
    public static void disconnectFromOpenFireServer()
            throws NotConnectedException {
        // TODO Auto-generated method stub

        try {
            openFireConnection.disconnect();
            if (asmk != null) {
                asmk.onDestroy();
                asmk = null;
            }
        } catch (Exception e) {

        }
    }

    // function to remove notifications icon from bars
    public static void cancellAllNotifications() throws NotConnectedException {
        // TODO Auto-generated method stub
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
    }

    // function to add user in roster
    public static void addRosterEntry(String rosterNameToAdd,
                                      String rosterNickNameToAdd) throws Exception {
        // TODO Auto-generated method stub
        openFireConnection.createEntry(rosterNameToAdd, rosterNickNameToAdd);

    }

    // Function to do something periodically
//    public void sendForAnHour() {
//
//        final Runnable sender = new Runnable() {
//            public void run() {
//                System.out.println("sent");
//                Log.d("F", "sendF");
//            }
//        };
//
//        senderHandle = scheduler.scheduleAtFixedRate(sender, 10, 10, SECONDS);
//
//        scheduler.schedule(new Runnable() {
//            public void run() {
//                senderHandle.cancel(true);
//            }
//        }, 60 * 60, SECONDS);
//    }

    // Function for cancel periodic job
    public static void sendForAnHourCancel() {

        senderHandle.cancel(true);
        System.out.println("schedule job cancelled");

    }

    // function to create a pub/sub node
    public static void createPubSubNode(String nodeName)
            throws NoResponseException, XMPPErrorException,
            NotConnectedException {

        openFireConnection.createPubSubNode(nodeName);
    }

    // function to subscribe to a node
    public static void subscribeToNode(String nodeName)
            throws NoResponseException, XMPPErrorException,
            NotConnectedException {

        openFireConnection.subscribePubSubNode(nodeName);
    }

    // function to publish to the node
    public static void publishToNode(String nodeName)
            throws NoResponseException, XMPPErrorException,
            NotConnectedException {

        openFireConnection.publishToPubSubNode(nodeName);
    }

    // Function to send AdHoc Command
    public static void sendAdhocCommand() throws XMPPException, SmackException {

        openFireConnection.sendAdHocCommands();
    }
}
