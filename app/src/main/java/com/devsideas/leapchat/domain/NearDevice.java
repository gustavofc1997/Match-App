package com.devsideas.leapchat.domain;

/**
 * Created by Gustavo Forer on 15/05/2017.
 */

public class NearDevice {
    private String mName;
    private String mToken;
    private String mPicture;


    public String getmPicture() {
        return mPicture;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }

    public String getmName() {
        return mName;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }
}
