import 'dart:async';

import 'package:flutter/services.dart';

class FlutterNotifications {
  static const MethodChannel _channel =
      const MethodChannel('flutter_notifications');

  static final String title = "Your task is ready!";
  static final String message = "It's time to do your task and check-up. Tap to go to it.";
  static final int snoozeDelay = 5;

  static void showNotification() async {
    await _channel.invokeMethod("showNotification", {
      "title": title,
      "message": message,
    });
  }

  static void scheduleNotification() async {
    await _channel.invokeMethod("scheduleNotification", {
      "title": title,
      "message": message,
      "offsetSec": snoozeDelay,
    });
  }

//  static void repeatNotification(Time startTime, Duration interval) async {
//    await _channel.invokeMethod("repeatNotification", {
//      "title": title,
//      "message": message,
//      "startTimeSec": startTime.second + startTime.minute * 60 + startTime.hour * 3600,
//      "intervalTimeSec": interval.inSeconds,
//    });
//  }

  static void cancelAll() async {
    await _channel.invokeMethod("cancelAll");
  }
}
