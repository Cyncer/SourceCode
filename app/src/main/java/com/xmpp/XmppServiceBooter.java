package com.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class XmppServiceBooter extends BroadcastReceiver {
	private String TAG = "BroadcastReceiver";
	
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "XMPP SERVICE BOOTED");
		Intent service = new Intent(context, XMPPService.class);
		service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startService(service);
	}

}
