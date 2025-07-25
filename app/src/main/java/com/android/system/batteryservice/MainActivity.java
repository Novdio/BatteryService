package com.android.system.batteryservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
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
}
