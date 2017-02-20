package com.devs.matchapp.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devs.matchapp.R;
import com.devs.matchapp.util.IntentHelper;

import butterknife.OnClick;

/**
 * Created by Gustavo on 2/12/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentHelper.goToSignUp(SplashActivity.this);
                finish();
            }
        }, 2500);
    }




}
