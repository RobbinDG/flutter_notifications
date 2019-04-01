import 'dart:async';

import 'package:flutter/services.dart';

class FlutterNotifications {
  static const MethodChannel _channel =
      const MethodChannel('flutter_notifications');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod("scheduleNotification", {
      "title": "Your task is ready!",
      "message": "It's time to do your task and check-up. Tap to go to it.",
      "offsetSec": 5,
    });
    return version;
  }
}
