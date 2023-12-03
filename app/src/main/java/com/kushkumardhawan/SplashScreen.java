package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import Presentation.CustomDialog;

public class SplashScreen extends AppCompatActivity {

    CustomDialog CD = new CustomDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//MainActivity
                Intent mainIntent = new Intent(SplashScreen.this, DownloadShowFile.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();


            }
        }, 5000);
    }


}