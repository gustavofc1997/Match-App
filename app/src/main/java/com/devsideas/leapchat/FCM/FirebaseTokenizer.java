package com.devsideas.leapchat.FCM;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.devsideas.leapchat.util.AppConstants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

/**
 * Created by Gustavo Forer on 14/05/2017.
 */

public class FirebaseTokenizer extends FirebaseInstanceIdService {


    private static final String TAG = "Firebase";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
