package com.devsideas.leapchat.util;

import com.google.android.gms.nearby.messages.Message;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.nio.charset.Charset;

/**
 * Used to prepare the payload for a
 * {@link com.google.android.gms.nearby.messages.Message Nearby Message}. Adds a unique id
 * to the Message payload, which helps Nearby distinguish between multiple devices with
 * the same model name.
 */
public class DeviceMessage {
    private static final Gson gson = new Gson();

    private final String mUUID;
    private final String mUserName;

    /**
     * Builds a new {@link Message} object using a unique identifier.
     */
    public static Message newNearbyMessage() {
        DeviceMessage deviceMessage = new DeviceMessage();
        return new Message(gson.toJson(deviceMessage).getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Creates a {@code DeviceMessage} object from the string used to construct the payload to a
     * {@code Nearby} {@code Message}.
     */
    public static DeviceMessage fromNearbyMessage(Message message) {
        String nearbyMessageString = new String(message.getContent()).trim();
        return gson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                DeviceMessage.class);
    }

    public String getmUUID() {
        return mUUID;
    }

    private DeviceMessage() {
        mUUID = FirebaseInstanceId.getInstance().getToken();
        mUserName = SharedPreferenceHelper.loadString(SharedPreferenceHelper.Name);
        // TODO(developer): add other fields that must be included in the Nearby Message payload.
    }

    public String getUserName() {
        return mUserName;
    }
}