package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class Splash extends AppCompatActivity {

    Intent intent;
    RotateAnimation rotate;
    ShapeableImageView img;
    ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img = findViewById(R.id.logo);
        rotate = new RotateAnimation(0,365);
        rotate.setDuration(900);
        img.startAnimation(rotate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}