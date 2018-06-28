package com.app.android.cync;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ApplicationController;
import com.cync.model.ChatMessage;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.Constants;
import com.xmpp.ChatAdapterNew;
import com.xmpp.ChatDatabase;
import com.xmpp.MyMessageListener;
import com.xmpp.Updatelistinterface;
import com.xmpp.XMPPService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends Activity implements Updatelistinterface,
        OnClickListener {


    ImageButton imageButtonBack;
    ChatManager chatmanager;
    String titlename = "Amigo";
    private static Handler mHandler = new Handler();
    public static ChatActivity mFinalChatActivity;
    private static ListView myList;

    TextView txtChatwithName;
    String toName = "";

    TextView btnSend;
    EditText edtText;
    String OUTGOING = "0";
    static String INMCOMING = "1";

    public static final int NOTIFICATION_ID = 1;
    static Context context;

    static ChatAdapterNew chatAdapterNew;
    static String sender;
    static ChatDatabase db;
    String type;
    Button btnLogout;
    ChatManagerListener chatManagerListener;
    public ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    MyMessageListener messageListenerInner;
    private String profileimage;


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);
    }


    //    Toolbar mToolbar;
    TextView mTitle;
    ImageView chat_back;
    ImageView chat_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);
        CommonClass.isForground = true;

//        mToolbar = (Toolbar) findViewById(R.id.appbar);
//
//        setSupportActionBar(mToolbar);
//
//
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitle = (TextView) findViewById(R.id.chat_toolbar_title);

        chat_img = (ImageView) findViewById(R.id.chat_img);
        chat_back = (ImageView) findViewById(R.id.chat_back);


        chat_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callNavicagation();
            }
        });


        myList = (ListView) this.findViewById(R.id.myList);
        btnSend = (TextView) this.findViewById(R.id.btnSend);
        edtText = (EditText) this.findViewById(R.id.edtText);

        edtText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {

                myList.setSelection(messages.size());

            }
        });

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        context = ChatActivity.this;


        Bundle bundle = getIntent().getExtras();

        if (bundle.getString("FROM_MESSGE") != null) {

            toName = bundle.getString("FROM_MESSGE");
            titlename = bundle.getString("titlename", "");
            titlename = titlename.toUpperCase();
            mTitle.setText(titlename.toUpperCase());
            type = bundle.getString("type");
            profileimage = bundle.getString("profileimage");

        }


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        db = new ChatDatabase(this);

        messages = db.GetmessageForFixUser(CommonClass.getChatUsername(this), toName, type);

        chatAdapterNew = new ChatAdapterNew(context, messages);
        myList.setAdapter(chatAdapterNew);

//        if (profileimage != null && profileimage.length() > 0) {
//            profileimage = Constants.imagBaseUrl + profileimage.trim();
//
//            Picasso.with(this).load(profileimage).into(chat_img);
//        }


        if (profileimage != null && profileimage.length() > 0) {
            if (profileimage.startsWith("http")) {
                profileimage = profileimage.trim();
                // Log.e("GroupInfo", "IF---------- " + profileimage);
            } else {
                // Log.e("GroupInfo", "ELSE---------- " + profileimage);
                profileimage = Constants.imagBaseUrl + profileimage.trim();
            }
            //Log.e("Picasso", "Final---------- " + profileimage);

            Picasso.with(this)
                    .load(profileimage)
                    .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(chat_img);

        } else {
            chat_img.setImageResource(R.drawable.no_image);

        }
    }

    private void callNavicagation() {

        finish();
//        Bundle bnd = new Bundle();
//        Intent intent = new Intent(context, NavigationDrawerActivity.class);
//
//        intent.setPackage(context.getPackageName());
////        bnd.putString("notification", "2");
//        intent.putExtra("notification", "4");
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        //finish();
    }

    protected void onResume() {

        super.onResume();
        //	 txtChatwithName=(TextView) findViewById(R.id.txtChatwithName);
        System.out.println("CHAT RESUME ");
        CommonClass.isForground = true;
        NotificationManager mNotificationManager = (NotificationManager)
                getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        mFinalChatActivity = this;
        db = new ChatDatabase(this);


        Bundle bundle = getIntent().getExtras();

        if (bundle.getString("FROM_MESSGE") != null) {

            toName = bundle.getString("FROM_MESSGE");
            titlename = bundle.getString("titlename");
            type = bundle.getString("type");
            profileimage = bundle.getString("profileimage");

            mTitle.setText(titlename.toUpperCase());

            if (profileimage != null && profileimage.length() > 0) {
                if (profileimage.startsWith("http")) {
                    profileimage = profileimage.trim();
                    // Log.e("GroupInfo", "IF---------- " + profileimage);
                } else {
                    // Log.e("GroupInfo", "ELSE---------- " + profileimage);
                    profileimage = Constants.imagBaseUrl + profileimage.trim();
                }
                //Log.e("Picasso", "Final---------- " + profileimage);
                //  Picasso.with(this).load(profileimage).into(chat_img);

                Picasso.with(this)
                        .load(profileimage)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(chat_img);
            }

        }


        messages = db.GetmessageForFixUser(CommonClass.getChatUsername(this), toName, type);

        chatAdapterNew = new ChatAdapterNew(context, messages);
        myList.setAdapter(chatAdapterNew);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // txtChatwithName.setText("" + titlename);

        if (messageListenerInner == null) {
            messageListenerInner = new MyMessageListener();
        }

    }


    public void sendMessage() {

        String mess = edtText.getText().toString().trim();

        if (!TextUtils.isEmpty(mess)) {
            // if (Constants.getUserpreference(getApplicationContext())
            // .getChatusername().equals("test456")) {
            // to = "test123";
            // }

            // if(toName.contains("jtcjtc"))
            // toName=toName.replace("jtcjtc", "jtc");
            System.out.println(" USER ********* " + toName);
            try {
                String userName = "";
                if (CommonClass.getUserpreference(this).first_name != null && CommonClass.getUserpreference(this).first_name.length() > 0)
                    userName = CommonClass.getUserpreference(this).first_name;
                if (CommonClass.getUserpreference(this).last_name != null && CommonClass.getUserpreference(this).last_name.length() > 0)
                    userName = userName + " " + CommonClass.getUserpreference(this).last_name;
//                System.out.println("ChatActivity.sendMessage"+userName);

                XMPPService.sendMessage(toName, "" + mess, type + "," + userName);

            } catch (NotConnectedException e)

            {
                // Log.i("", "Exception===" + e.getLocalizedMessage());
                // Log.d("Send msg", "cannot Send A msg: " +
                // e.getMessage());
            }

            try {
                db.INSERT_NEW_MESSAGE(CommonClass.getChatUsername(this), toName, mess,
                        OUTGOING, CommonClass.getCurrentTimeStamp(), "false", type);
            } catch (Exception e) {
                // Log.i("", "Message insert exception=" + e.getLocalizedMessage());
            }

            messages = db
                    .GetmessageForFixUser(CommonClass.getChatUsername(this), toName, type);

            setListAdapter();
            edtText.setText("");
        }
    }

    private void setListAdapter() {
        for (int i = 0; i < messages.size(); i++) {
            System.out.println(messages.get(i).isLeft());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapterNew = new ChatAdapterNew(context, messages);
                myList.setAdapter(chatAdapterNew);
                myList.setSelection(messages.size());

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonClass.isForground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonClass.isForground = false;
        if (chatManagerListener != null)
            chatmanager.removeChatListener(chatManagerListener);
    }

    private String capitalize(String line) {

        try {

            char[] chars = line.toLowerCase().toCharArray();
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
                if (!found && Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    found = true;
                } else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
                        || chars[i] == '\'') { // You can add other chars here
                    found = false;
                }
            }
            return String.valueOf(chars);
        } catch (NullPointerException e) {
            return line;
        }
    }

    public boolean isForeground(String PackageName) {
        // Get the Activity Manager
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

        // Get the info we need for comparison.
        ComponentName componentInfo = task.get(0).topActivity;

        // Check if it matches our package name.
        if (componentInfo.getPackageName().equals(PackageName))
            return true;

        // If not then our app is not on the foreground.
        return false;
    }


    public void onClick(View v) {


    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        CommonClass.isForground = false;
        callNavicagation();
//        Intent intent = new Intent(context, NavigationDrawerActivity.class);
//        intent.setPackage(context.getPackageName());
//        intent.putExtra("notification", "2");
//        startActivity(intent);
//        finish();
    }

    @Override
    public void onNewMessage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refrehsList(Chat chat, Message message, String profile) {
        // TODO Auto-generated method stub


        String from = message.getFrom();
        String body = message.getBody().trim();
        // String sub = message.getSubject();
        String sub[] = message.getSubject().split(",");
        // if (isForeground("com.Amigo.FinalChatActivity"))
        if (CommonClass.isForground) {
//            Log.i("", String.format(
//                    "Received message frontend '%1$s' from %2$s", body, from));

            if (from.contains("@")) {
                from = from.substring(0, from.indexOf("@"));

                // from=
                // from.substring(0,from.indexOf("@Amigoapp.com/Smack"));

//                Log.i("", "Title === outer toName" + from);
            }


            if (toName.equalsIgnoreCase(from)) {


                toName = from;
                titlename = sub[0];
                type = sub[1];
//            String sub= CommonClass.strEncodeDecode(titlename,true);
                mTitle.setText(titlename.toUpperCase());
                System.out.println("*****toName refresh****" + toName);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //	txtChatwithName.setText("" + titlename );
                    }
                });

                messages = db.GetmessageForFixUser(CommonClass.getChatUsername(this), from, type);

                mHandler.post(new Runnable() {

                    public void run() {
                        setListAdapter();
                    }
                });
            } else {

                String mProfile = "";
                if (sub.length == 4) {
                    mProfile = sub[3];
                } else {
                    mProfile = "";
                }

                createNotification(from, body, sub[0], sub[1], mProfile);
            }

        }
    }


    private void createNotification(String from, String body, String sub,
                                    String sub2, String profile) {
        /*if (CommonClass.getChatPref(ApplicationController.getContext()) == true)
        {

          // based on whether chat notifications are on/off in settings or preference screen

		}*/
//		sub= CommonClass.strEncodeDecode(sub,true);
        NotificationManager mNotificationManager = (NotificationManager) ApplicationController
                .getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        System.out.println("-------Getting mNotificationManager: "
                + mNotificationManager);

        Intent intent = new Intent(ApplicationController.getContext(),
                ChatActivity.class);
        intent.putExtra("FROM_MESSGE", from);
        intent.putExtra("titlename", sub);
        intent.putExtra("type", sub2);
        intent.putExtra("profileimage", profile);

        Log.i("", "Notification= From=" + from);
        Log.i("", "Notification= titlename=" + sub);
        Log.e("Chat Xmpp", "Notification= body=" + body);
        Log.i("", "Notification= image=" + profile);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(
                ApplicationController.getContext(), iUniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                ApplicationController.getContext())
                .setSmallIcon(getNotificationIcon())
                .setContentTitle("" + sub)
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(body);
//				.setTicker(body);


        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.getNotification();
        notification.contentView.setImageViewResource(android.R.id.icon, R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(
                Calendar.getInstance().get(Calendar.MILLISECOND),
                mBuilder.getNotification());


    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }
}
