package com.devsideas.leapchat.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.devsideas.leapchat.BuildConfig;
import com.devsideas.leapchat.R;
import com.devsideas.leapchat.util.AppConstants;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.ConfirmationCodeCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;

public class SignUpActivity extends ImageGalleryActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ri1Tekkh5lrSwABgGafCGVOdO";
    private static final String TWITTER_SECRET = "QJ8Kbqd32NRwgywtRNm0wzrkSOAfAHFQEgFQPpTboxlH24disA";

    @Bind(R.id.txt_name)
    EditText mTxtName;
    @Bind(R.id.txt_phone)
    EditText mTxtPhone;
    @Bind(R.id.radioGroup_genre)
    RadioGroup mRadioGroup;
    @Bind(R.id.radio_man)
    RadioButton mRadioMan;
    @Bind(R.id.radio_other)
    RadioButton mRadioOther;
    @Bind(R.id.radio_woman)
    RadioButton mRadioWoman;
    @Bind(R.id.toolbar_signup)
    Toolbar mActionBar;
    @Bind(R.id.spin_interested)
    BetterSpinner mSpin;
    @Bind(R.id.img_photo1)
    CircleImageView mImgPhoto1;
    @Bind(R.id.img_photo2)
    CircleImageView mImgPhoto2;
    @Bind(R.id.img_photo3)
    CircleImageView mImgPhoto3;
    private String pic1 = "Picture1";
    private String pic2 = "Picture2";
    private String pic3 = "Picture3";
    private String path1 = "";
    private String path2 = "";
    private String path3 = "";
    private String mGenre = "";
    private String mInterested = "";
    private AuthCallback authCallback;

    private static final String[] INTERESTED = new String[]{
            "Hombres", "Mujeres", "Ambos"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
            }
        };
        ButterKnife.bind(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, INTERESTED);
        mSpin.setAdapter(adapter);
        mImgPhoto1.setOnClickListener(this);
        mImgPhoto2.setOnClickListener(this);
        mImgPhoto3.setOnClickListener(this);
    }

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

    @OnClick(R.id.lab_signup)
    void signUp() {
//        if (!path1.equals("") && !path2.equals("") && !path3.equals("")) {
        if (!mTxtName.getText().toString().equals("") && mTxtName.getText().toString().length() > 4) {
            if (!mTxtPhone.getText().toString().equals("") && mTxtPhone.getText().length() == 10) {
                if (!mGenre.equals("")) {
//                        if (!mInterested.equals("")) {
                    AuthConfig.Builder digitsAuthConfigBuilder = new AuthConfig.Builder()
                            .withAuthCallBack(authCallback)
//                            .withCustomPhoneNumberScreen(confirmationCodeCallback)
//                            .withPartnerKey(BuildConfig.PARTNER_KEY)
                            .withPhoneNumber("+57" + mTxtPhone.getText().toString());
                    Digits.authenticate(digitsAuthConfigBuilder.build());
//                        } else
//                            Toasty.warning(this, getString(R.string.miss_interested), Toast.LENGTH_SHORT, true).show();
                } else
                    Toasty.warning(this, getString(R.string.miss_genre), Toast.LENGTH_SHORT, true).show();
            } else
                Toasty.warning(this, getString(R.string.miss_phone), Toast.LENGTH_SHORT, true).show();
        } else
            Toasty.warning(this, getString(R.string.miss_name), Toast.LENGTH_SHORT, true).show();
//        } else
//            Toasty.warning(this, getString(R.string.miss_pictures), Toast.LENGTH_SHORT, true).show();
    }


    final ConfirmationCodeCallback confirmationCodeCallback = new ConfirmationCodeCallback() {
        @Override
        public void success(Intent intent) {
            startActivity(intent);
        }

        @Override
        public void failure(DigitsException error) {
            Toasty.error(SignUpActivity.this, error.getMessage(), Toast.LENGTH_LONG, true).show();
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.radio_man:
                mGenre = AppConstants.MEN;
                mRadioMan.setTextColor(Color.WHITE);
                mRadioWoman.setTextColor(getResources().getColor(R.color.color_unselected));
                mRadioOther.setTextColor(getResources().getColor(R.color.color_unselected));
                break;
            case R.id.radio_woman:
                mGenre = AppConstants.WOMEN;
                mRadioWoman.setTextColor(Color.WHITE);
                mRadioMan.setTextColor(getResources().getColor(R.color.color_unselected));
                mRadioOther.setTextColor(getResources().getColor(R.color.color_unselected));
                break;
            case R.id.radio_other:
                mGenre = AppConstants.OTHER;
                mRadioOther.setTextColor(Color.WHITE);
                mRadioMan.setTextColor(getResources().getColor(R.color.color_unselected));
                mRadioWoman.setTextColor(getResources().getColor(R.color.color_unselected));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_signup:
                finish();
                break;
            case R.id.img_photo1:
                showDialogPicturesOptions(R.id.img_photo1, pic1);
                break;
            case R.id.img_photo2:
                showDialogPicturesOptions(R.id.img_photo2, pic2);
                break;
            case R.id.img_photo3:
                showDialogPicturesOptions(R.id.img_photo3, pic3);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PICTURE:
                break;
            case PHOTO_CODE:
                break;
        }
    }
}
