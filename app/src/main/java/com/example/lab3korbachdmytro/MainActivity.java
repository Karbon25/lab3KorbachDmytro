package com.example.lab3korbachdmytro;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3korbachdmytro.Service.ObserverService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, ObserverService.class);
        startService(serviceIntent);


        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                changeActivity();
            }
        };
        timer.start();
        findViewById(R.id.main_activity_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                changeActivity();
            }
        });
    }
    private void changeActivity(){
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Intent serviceIntent = new Intent(this, ObserverService.class);
        stopService(serviceIntent);
        super.onDestroy();
    }
}