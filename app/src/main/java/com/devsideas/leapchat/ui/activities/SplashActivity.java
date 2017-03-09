package com.devsideas.leapchat.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devsideas.leapchat.R;
import com.devsideas.leapchat.util.IntentHelper;
import com.devsideas.leapchat.util.SharedPreferenceHelper;

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
                if (SharedPreferenceHelper.loadBoolean(SharedPreferenceHelper.ACTIVE))
                    IntentHelper.goToHome(SplashActivity.this);
                else
                    IntentHelper.goToSignUp(SplashActivity.this);
                finish();
            }
        }, 2500);
    }


}
