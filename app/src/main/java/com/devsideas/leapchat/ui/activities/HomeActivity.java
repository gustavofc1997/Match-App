package com.devsideas.leapchat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.devsideas.leapchat.R;
import com.devsideas.leapchat.chat.MainActivity;
import com.devsideas.leapchat.domain.NearDevice;
import com.devsideas.leapchat.ui.activities.adapters.RecyclerAdapter;
import com.devsideas.leapchat.util.DeviceMessage;
import com.devsideas.leapchat.util.ItemClick;
import com.devsideas.leapchat.util.RecyclerListener;
import com.devsideas.leapchat.util.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gustavofc97 on 2/19/2017.
 */

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int TTL_IN_SECONDS = 3 * 60; // Three minutes.
    private FirebaseAuth mAuth;


    // Key used in writing to and reading from SharedPreferences.
    private static final String KEY_UUID = "key_uuid";

    /**
     * Sets the time in seconds for a published message or a subscription to live. Set to three
     * minutes in this sample.
     */
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(TTL_IN_SECONDS).build();

    /**
     * Creates a UUID and saves it to {@link SharedPreferences}. The UUID is added to the published
     * message to avoid it being undelivered due to de-duplication. See {@link com.devsideas.leapchat.util.DeviceMessage} for
     * details.
     */
    private static String getUUID(SharedPreferences sharedPreferences) {
        String uuid = sharedPreferences.getString(KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(KEY_UUID, uuid).apply();
        }
        return uuid;
    }

    /**
     * The entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;

    // Views.
    private HashMap<String, NearDevice> mDevices;
    private SwitchCompat mPublishSwitch;
    private SwitchCompat mSubscribeSwitch;
    private ArrayList<NearDevice> mList;
    private LinearLayoutManager mLayoutManager;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    /**
     * The {@link Message} object used to broadcast information about the device to nearby devices.
     */
    private Message mPubMessage;

    /**
     * A {@link MessageListener} for processing messages from nearby devices.
     */
    private MessageListener mMessageListener;
    private RecyclerView mRecyclerNearDevices;
    /**
     * Adapter for working with messages from nearby publishers.
     */
    private RecyclerAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("phone")) {
                String title = getIntent().getExtras().getString("phone");
                String[] result = title.split(",");
                showAlert(result[0], result[1], getIntent().getExtras().getString("body"), getIntent().getExtras().getString("picture"));
            } else {
                Intent chat = new Intent(HomeActivity.this, MainActivity.class);
                chat.putExtra("phone", getIntent().getExtras().getString("chat"));
                startActivity(chat);
            }
        }
        mList = new ArrayList<>();
        mDevices = new HashMap<>();
        mRecyclerNearDevices = (RecyclerView) findViewById(R.id.nearby_devices_list_view);
        mSubscribeSwitch = (SwitchCompat) findViewById(R.id.subscribe_switch);
        mPublishSwitch = (SwitchCompat) findViewById(R.id.publish_switch);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword(SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone) + "@leapchat.com", SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        Toast.makeText(HomeActivity.this, "Auth Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        mPubMessage = DeviceMessage.newNearbyMessage();
        // Build the message that is going to be published. This contains the device name and a
        // UUID.
        mPubMessage = DeviceMessage.newNearbyMessage();
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                // Called when a new message is found.
                NearDevice device = new NearDevice();
                device.setmPicture(DeviceMessage.fromNearbyMessage(message).getmUrlPhoto());
                device.setmName(DeviceMessage.fromNearbyMessage(message).getUserName());
                device.setmToken(DeviceMessage.fromNearbyMessage(message).getmUUID());
                mDevices.put(device.getmName(), device);
                mAdapter.setItems(getList());
            }

            @Override
            public void onLost(final Message message) {
                // Called when a message is no longer detectable nearby.
                mDevices.remove(DeviceMessage.fromNearbyMessage(message).getUserName());
                mAdapter.setItems(getList());
            }
        };


        mSubscribeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If GoogleApiClient is connected, perform sub actions in response to user action.
                // If it isn't connected, do nothing, and perform sub actions when it connects (see
                // onConnected()).
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (isChecked) {
                        subscribe();
                    } else {
                        unsubscribe();
                    }
                }
            }
        });

        mPublishSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If GoogleApiClient is connected, perform pub actions in response to user action.
                // If it isn't connected, do nothing, and perform pub actions when it connects (see
                // onConnected()).
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (isChecked) {
                        publish();
                    } else {
                        unpublish();
                    }
                }
            }
        });

        mAdapter = new RecyclerAdapter(HomeActivity.this);
        if (mRecyclerNearDevices != null) {
            mRecyclerNearDevices.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
            mRecyclerNearDevices.setAdapter(mAdapter);
        }
        mAdapter.setItemClickListener(new RecyclerListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    onUserSelected(mAdapter.getitem(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        buildGoogleApiClient();
    }

    private ArrayList<NearDevice> getList() {
        ArrayList<NearDevice> mDevice = new ArrayList<>();
        Iterator it = mDevices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            mDevice.add((NearDevice) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return mDevice;

    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mPublishSwitch.setEnabled(false);
        mSubscribeSwitch.setEnabled(false);
        logAndShowSnackbar("Exception while connecting to Google Play services: " +
                connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {
        logAndShowSnackbar("Connection suspended. Error code: " + i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        // We use the Switch buttons in the UI to track whether we were previously doing pub/sub (
        // switch buttons retain state on orientation change). Since the GoogleApiClient disconnects
        // when the activity is destroyed, foreground pubs/subs do not survive device rotation. Once
        // this activity is re-created and GoogleApiClient connects, we check the UI and pub/sub
        // again if necessary.
        if (mPublishSwitch.isChecked()) {
            publish();
        }
        if (mSubscribeSwitch.isChecked()) {
            subscribe();
        }
    }

    /**
     * Subscribes to messages from nearby devices and updates the UI if the subscription either
     * fails or TTLs.
     */
    private void subscribe() {
        Log.i(TAG, "Subscribing");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer subscribing");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSubscribeSwitch.setChecked(false);
                            }
                        });
                    }
                }).build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed successfully.");
                        } else {
                            logAndShowSnackbar("Could not subscribe, status = " + status);
                            mSubscribeSwitch.setChecked(false);
                        }
                    }
                });
    }

    /**
     * Publishes a message to nearby devices and updates the UI if the publication either fails or
     * TTLs.
     */
    private void publish() {
        Log.i(TAG, "Publishing");
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer publishing");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPublishSwitch.setChecked(false);
                            }
                        });
                    }
                }).build();

        Nearby.Messages.publish(mGoogleApiClient, mPubMessage, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Published successfully.");
                        } else {
                            logAndShowSnackbar("Could not publish, status = " + status);
                            mPublishSwitch.setChecked(false);
                        }
                    }
                });
    }

    /**
     * Stops subscribing to messages from nearby devices.
     */
    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    /**
     * Stops publishing message to nearby devices.
     */
    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
    }

    /**
     * Logs a message and shows a {@link Snackbar} using {@code text};
     *
     * @param text The text used in the Log message and the SnackBar.
     */
    private void logAndShowSnackbar(final String text) {
        Log.w(TAG, text);
        View container = findViewById(R.id.activity_main_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    private void showAlert(final String phone, final String tokentosend, String body, String pic) {
        try {
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setCancelText(getString(android.R.string.no))
                    .setTitleText("Alguien quiere conocerte!")
                    .setCustomImage(drawableFromUrl(pic))
                    .setConfirmText(getString(android.R.string.yes))
                    .setContentText(body)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            chatAccepted(SharedPreferenceHelper.loadString(SharedPreferenceHelper.Name), SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone), tokentosend);
                            sDialog.dismissWithAnimation();
                            Intent chat = new Intent(HomeActivity.this, MainActivity.class);
                            chat.putExtra("phone", phone);
                            startActivity(chat);

                        }
                    })
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            finish();
        }
    }


    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    private void chatAccepted(final String name, final String phone, final String token) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", name + " ha aceptado tu invitacion");
                    dataJson.put("title", phone);
                    json.put("notification", dataJson);
                    json.put("to", token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AAAAIcrqQR4:APA91bGWEQnSElpdJH-HSV-FEDHJJSJNE45dtdSml3JdFY0G-XATiV-L9jiMa5D-MGQMhH3qjPezkNo8MhzznkOtWYJMrlrlgBXxNb3YR989mCxxpRCbokQiQu9reBvxhKKft3q0Vrag")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    Log.d(TAG, e + "Catch");
                }
                return null;
            }
        }.execute();

    }

    private void sendNotification(final String mytoken, final String reg_token, final String name, final String phone) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", "Quieres conocer a " + name + "?" + "," + SharedPreferenceHelper.loadString(SharedPreferenceHelper.MY_PIC));
                    dataJson.put("title", phone + "," + mytoken);
                    json.put("notification", dataJson);
                    json.put("to", reg_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AAAAIcrqQR4:APA91bGWEQnSElpdJH-HSV-FEDHJJSJNE45dtdSml3JdFY0G-XATiV-L9jiMa5D-MGQMhH3qjPezkNo8MhzznkOtWYJMrlrlgBXxNb3YR989mCxxpRCbokQiQu9reBvxhKKft3q0Vrag")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    System.out.println(finalResponse);
                } catch (Exception e) {
                    Log.d(TAG, e + "");
                }
                return null;
            }
        }.execute();

    }

    private void onUserSelected(final NearDevice device) throws IOException {
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.interested))
                .setCustomImage(drawableFromUrl(device.getmPicture()))
                .setCancelText(getString(android.R.string.no))
                .setConfirmText(getString(android.R.string.yes))
                .setContentText(getString(R.string.meet_people))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        // reuse previous dialog instance
                        sendNotification(FirebaseInstanceId.getInstance().getToken(), device.getmToken(), device.getmName(), SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone));
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }
}
