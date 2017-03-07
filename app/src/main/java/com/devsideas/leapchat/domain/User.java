package com.devsideas.leapchat.domain;

import java.util.ArrayList;

/**
 * Created by Gustavo on 06/03/2017.
 */

public class User {

    private String mName;
    private ArrayList<String> mPictures;
    private String mGenre;
    private String mInterestedIn;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public ArrayList<String> getmPictures() {
        return mPictures;
    }

    public void setmPictures(ArrayList<String> mPictures) {
        this.mPictures = mPictures;
    }

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getmInterestedIn() {
        return mInterestedIn;
    }

    public void setmInterestedIn(String mInterestedIn) {
        this.mInterestedIn = mInterestedIn;
    }
}
