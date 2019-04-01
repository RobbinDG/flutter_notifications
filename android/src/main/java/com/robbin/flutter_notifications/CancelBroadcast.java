package com.robbin.flutter_notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

public class CancelBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle data = intent.getExtras();
		try {
			int id = data.getInt(context.getString(R.string.notification_broadcast_id));
			FlutterNotificationsPlugin.cancelById(context, id);
		} catch (NullPointerException e) {
			System.err.println("Could not retrieve notification id from broadcast");
			e.printStackTrace();
		}
	}
}
