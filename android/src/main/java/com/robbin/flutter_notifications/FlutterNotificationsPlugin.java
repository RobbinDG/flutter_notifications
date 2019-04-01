package com.robbin.flutter_notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterNotificationsPlugin
 */
public class FlutterNotificationsPlugin implements MethodCallHandler {
	private static final String CHANNEL_ID = "test channel";
	private static final String ACTION_OPEN = "com.robbin.flutter_notifications.OPEN";
	private static final String ACTION_SNOOZE = "com.robbin.flutter_notifications.SNOOZE";
	private static final String ACTION_CANCEL = "com.robbin.flutter_notifications.CANCEL";
	private static final String ACTION_SCHEDULE = "com.robbin.flutter_notifications.SCHEDULE";
	private Registrar registrar;

	public FlutterNotificationsPlugin(Registrar registrar) {
		this.registrar = registrar;
		createNotificationChannel(registrar.context());
	}

	private static Class getMainActivityClass(Context context) {
		String packageName = context.getPackageName();
		Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		String className = launchIntent.getComponent().getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static NotificationCompat.Builder getBuilder(Context context, String title, String message) {
		Intent tapIntent = new Intent(context, getMainActivityClass(context));
		tapIntent.setAction(ACTION_OPEN);
//		intent.putExtra(PAYLOAD, notificationDetails.payload);
		PendingIntent tapPendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent snoozeIntent = new Intent(context, SnoozeBroadcast.class);
		snoozeIntent.setAction(ACTION_SNOOZE);
		snoozeIntent.putExtra(context.getString(R.string.notification_broadcast_id), 0);
		snoozeIntent.putExtra("title", title);
		snoozeIntent.putExtra("message", message);
		PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

		Intent cancelIntent = new Intent(context, CancelBroadcast.class);
		cancelIntent.setAction(ACTION_CANCEL);
		cancelIntent.putExtra(context.getString(R.string.notification_broadcast_id), 0);
		PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);

		// TODO: get channel from arguments
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle(title)
				.setContentText(message)
				.setContentIntent(tapPendingIntent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.addAction(R.drawable.notification_icon, "Snooze", snoozePendingIntent)
				.addAction(R.drawable.notification_icon, "Cancel", cancelPendingIntent)
				.setAutoCancel(true);
		return builder;
	}

	public static void showNotification(Context context, String title, String message) {
		NotificationCompat.Builder builder = getBuilder(context, title, message);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(0, builder.build());
	}

	public static void scheduleNotification(Context context, String title, String message, long offsetSec) {
		NotificationCompat.Builder builder = getBuilder(context, title, message);

		Intent scheduleIntent = new Intent(context, ScheduleBroadcast.class);
		scheduleIntent.setAction(ACTION_SCHEDULE);
		scheduleIntent.putExtra(ScheduleBroadcast.NOTIFICATION, builder.build());
		scheduleIntent.putExtra(ScheduleBroadcast.NOTIFICATION_ID, 0);
		PendingIntent schedulePendingIntent = PendingIntent.getBroadcast(context, 0, scheduleIntent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * offsetSec, schedulePendingIntent);
	}

	public static void repeatNotification(Context context, String title, String message, long intervalSec, long startTimeSec) {
		NotificationCompat.Builder builder = getBuilder(context, title, message);

		Intent scheduleIntent = new Intent(context, ScheduleBroadcast.class);
		scheduleIntent.setAction(ACTION_SCHEDULE);
		scheduleIntent.putExtra(ScheduleBroadcast.NOTIFICATION, builder.build());
		scheduleIntent.putExtra(ScheduleBroadcast.NOTIFICATION_ID, 0);
		PendingIntent schedulePendingIntent = PendingIntent.getBroadcast(context, 0, scheduleIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * startTimeSec,
				1000 * intervalSec, schedulePendingIntent);

	}

	public static void cancelById(Context context, int id) throws NullPointerException {
		NotificationManagerCompat manager = NotificationManagerCompat.from(context);
		manager.cancel(id);
	}

	public static void cancelAll(Context context) {
		NotificationManagerCompat manager = NotificationManagerCompat.from(context);
		manager.cancelAll();
	}

	/**
	 * Plugin registration.
	 */
	public static void registerWith(Registrar registrar) {
		FlutterNotificationsPlugin plugin = new FlutterNotificationsPlugin(registrar);
		final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_notifications");
		channel.setMethodCallHandler(plugin);
	}

	@Override
	public void onMethodCall(MethodCall call, Result result) {
		String title;
		String message;
		try {
			switch (call.method) {
				case "showNotification":
					title = call.argument("title");
					message = call.argument("message");
					showNotification(registrar.context(), title, message);
					break;
				case "scheduleNotification":
					title = call.argument("title");
					message = call.argument("message");
					Integer offsetSec = call.argument("offsetSec");
					scheduleNotification(registrar.context(), title, message, offsetSec);
					break;
				case "repeatNotification":
					title = call.argument("title");
					message = call.argument("message");
					Integer startTimeSec = call.argument("startTimeSec");
					Integer intervalSec = call.argument("intervalSec");
					repeatNotification(registrar.context(), title, message, intervalSec, startTimeSec);
					break;
				case "cancelById":
					Integer id = call.argument("id");
					cancelById(registrar.context(), id);
					break;
				case "cancelAll":
					cancelAll(registrar.context());
					break;
				default:
					result.notImplemented();
					break;
			}
		} catch (NullPointerException e) {
			result.error(null, "Arguments not found", null);
		}
	}

	private void createNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getString(R.string.channel_name);
			String description = context.getString(R.string.channel_description);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			NotificationManager manager = context.getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel);
		}
	}
}
