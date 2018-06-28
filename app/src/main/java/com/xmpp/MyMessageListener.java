package com.xmpp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.ChatActivity;
import com.app.android.cync.R;
import com.utils.CommonClass;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Calendar;
import java.util.List;


public class MyMessageListener implements MessageListener {
	public static final int NOTIFICATION_ID = 1;
	ChatDatabase db;
	String INMCOMING = "1";

	Handler mHandler;

	@Override
	public void processMessage(final Chat chat, final Message message) {
		// TODO Auto-generated method stub
		System.out.println("In process message");


		System.out.println("In process message");
		String from = message.getFrom();
		String body = message.getBody().trim();
		final String sub[] = message.getSubject().split(",");





		if (from.contains("@")) {
			from = from.substring(0, from.indexOf("@"));

			// from=
			// from.substring(0,from.indexOf("@Amigoapp.com/Smack"));

			Log.i("", "Title === outer toName" + from);
		}



		CommonClass.setFriendsChat(ApplicationController.getContext(),from,sub[0],sub[3]);
		System.out.println("  ####   "+from+" ****** "+sub[0]+"  &&&&&&  "+sub[3]);

		db = new ChatDatabase(ApplicationController
				.getContext());

		if (!CommonClass.isForground)
		{
			Log.i("",
					String.format(
							"Received message backend '%1$s' from %2$s",
							body, from));

			db.INSERT_NEW_MESSAGE(CommonClass
							.getChatUsername(ApplicationController
									.getContext()), from,
					message.getBody().trim(),
					INMCOMING, CommonClass
							.getCurrentTimeStamp(),
					"false", sub[1]);


			// if((CommonClass.getChatPref(AppController.getInstance()))
			// == true);
			// {
			// System.out.println("IS CHAT REALLY ON "+
			// CommonClass.getChatPref(AppController.getContext()));
			if(sub.length==4)
			{
				createNotification(from, body, sub[0],
						sub[1],sub[3]);
			}
			else
			{
				createNotification(from, body, sub[0],
						sub[1],"");
			}
			// }
		}
		else
		{
			db.INSERT_NEW_MESSAGE(CommonClass
							.getChatUsername(ApplicationController
									.getContext()), from,
					message.getBody().trim(),
					INMCOMING, CommonClass
							.getCurrentTimeStamp(),
					"true", sub[1]);

			Handler handler = new Handler(Looper
					.getMainLooper());

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Updatelistinterface uu = ChatActivity.mFinalChatActivity;
					System.out
							.println("Calling refreshlist"+sub[3]);

					uu.refrehsList(chat, message,sub[3]);
				}
			}, 1); // handler
		}












//		String from = message.getFrom();
//		String body = message.getBody().trim();
//		final String sub[] = message.getSubject().split(",");
//
//		if (from.contains("@")) {
//			from = from.substring(0, from.indexOf("@"));
//
//			// from=
//			// from.substring(0,from.indexOf("@Amigoapp.com/Smack"));
//
//			Log.i("", "Title === outer toName" + from);
//		}
//
//		db = new ChatDatabase(ApplicationController.getContext());
//		if (!CommonClass.isForground)
//		{
//			Log.i("", String.format(
//					"Received message backend '%1$s' from %2$s", body, from));
//
//			db.INSERT_NEW_MESSAGE(
//					CommonClass.getUserpreference(ApplicationController.getContext())
//							.user_id, from, message.getBody().trim(),
//					INMCOMING, CommonClass.getCurrentTimeStamp(), "false",
//					sub[1]);
//
//			Log.i("", "Notification in Mymessagelistner");
//
//			// See Chat Preference............
//
//			/*if(Master_Activity.ischatOn)
//			{
//
//			}*/
//			createNotification(from, body, sub[0], sub[1]);
//		}
//		else
//		{
//			db.INSERT_NEW_MESSAGE(
//					CommonClass.getUserpreference(ApplicationController.getContext())
//							.user_id, from, message.getBody().trim(),
//					INMCOMING, CommonClass.getCurrentTimeStamp(), "true",
//					sub[1]);
//
//			Handler handler = new Handler(Looper.getMainLooper());
//
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Updatelistinterface uu = ChatActivity.mFinalChatActivity;
//					System.out.println("Calling refreshlist");
//					uu.refrehsList(chat, message,sub[2]);
//				}
//			}, 1);
//
//			// UpdateList updateList = (UpdateList)FinalChatActivity.class;
//
//		}

	}

//	private void createNotification(String from, String body, String sub,
//			String sub2) {
//		// TODO Auto-generated method stub
//		NotificationManager mNotificationManager = (NotificationManager) ApplicationController
//				.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//		System.out.println("-------Getting mNotificationManager: "
//				+ mNotificationManager);
//
//		Intent intent = new Intent(ApplicationController.getContext(),
//				ChatActivity.class);
//		intent.putExtra("FROM_MESSGE", from);
//		intent.putExtra("titlename", sub);
//		intent.putExtra("type", sub2);
//
//		Log.i("", "Notification= From=" + from);
//		Log.i("", "Notification= titlename=" + sub);
//		Log.i("", "Notification= body=" + body);
//
//		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//		int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
//		PendingIntent contentIntent = PendingIntent.getActivity(
//				ApplicationController.getContext(), iUniqueId, intent,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//				ApplicationController.getContext())
//				.setSmallIcon(R.drawable.cync_icon).setContentTitle("" + sub)
//
//				.setContentText(body);
//
//		mBuilder.setContentIntent(contentIntent);
//		mBuilder.setAutoCancel(true);
//
//
//
////		int tmp=0;
////		if (from.contains("cync_"))
////			tmp =Integer.parseInt( from.substring(0, from.indexOf("cync_")));
//
//
//
//		mNotificationManager
//				.notify(NOTIFICATION_ID, mBuilder.getNotification());
//
//	}


	private void createNotification(String from, String body, String sub,
									String sub2,String profile) {
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
		if(alarmSound == null){
			alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if(alarmSound == null){
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
		notification.contentView.setImageViewResource(android.R.id.icon,R.mipmap.ic_launcher);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(
				Calendar.getInstance().get(Calendar.MILLISECOND),
				mBuilder.getNotification());
	}

	private int getNotificationIcon() {
		boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
		return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
	}


	public boolean isForeground(String PackageName) {

		ActivityManager manager = (ActivityManager) ApplicationController.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

		ComponentName componentInfo = task.get(0).topActivity;

		if (componentInfo.getPackageName().equals(PackageName))
			return true;

		return false;
	}

	// @Override
	// public void refrehsList() {
	// //FinalChatActivity finalChatActivity =
	// (FinalChatActivity)MyMessageListener.class;
	//
	// }

}
