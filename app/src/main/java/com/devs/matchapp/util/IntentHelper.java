package com.devs.matchapp.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.devs.matchapp.ui.activities.LoginActivity;
import com.devs.matchapp.ui.activities.SignUpActivity;

/**
 * Created by Gustavo on 2/12/2017.
 */

public class IntentHelper {


    public static void goToLogin(Activity activity) {
        launchIntent(activity, LoginActivity.class);
    }

    public static void goToSignUp(Activity activity) {
        launchIntent(activity, SignUpActivity.class);

    }

    // generic methods
    public static <T> void launchIntent(Activity activity, Class<T> className) {
        launchIntent(activity, className, -1, null);
    }

    public static <T> void launchIntent(Activity activity, Class<T> className,
                                        int flags) {
        launchIntent(activity, className, flags, null);
    }

    public static <T> void launchIntent(Activity activity, Class<T> className,
                                        int flags, Bundle extras) {
        Intent intent = new Intent(activity, className);

        if (flags >= 0)
            intent.setFlags(flags);

        if (extras != null)
            intent.putExtras(extras);
        activity.startActivity(intent);
    }

}
