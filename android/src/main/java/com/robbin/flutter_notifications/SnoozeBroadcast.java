package com.robbin.flutter_notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SnoozeBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle data = intent.getExtras();
		try {
			int id = data.getInt(context.getString(R.string.notification_broadcast_id));
			FlutterNotificationsPlugin.cancelById(context, id);
			// TODO: let caller provide snooze delay
			FlutterNotificationsPlugin.scheduleNotification(context, data.getString("title"), data.getString("message"), 5);
		} catch (NullPointerException e) {
			System.err.println("Could not retrieve notification id from broadcast");
			e.printStackTrace();
		}
	}
}
