package com.robbin.flutter_notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

public class ScheduleBroadcast extends BroadcastReceiver {
	public static final String NOTIFICATION = "notification";
	public static final String NOTIFICATION_ID = "notification_id";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle data = intent.getExtras();
		Notification n = data.getParcelable(NOTIFICATION);
		int id = data.getInt(NOTIFICATION_ID);

		NotificationManagerCompat manager = NotificationManagerCompat.from(context);
		manager.notify(id, n);
	}
}
