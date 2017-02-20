package com.devs.matchapp.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.devs.matchapp.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends ImageGalleryActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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

    private static final String[] INTERESTED = new String[]{
            "Hombres", "Mujeres", "Ambos"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        ButterKnife.bind(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, INTERESTED);
        mSpin.setAdapter(adapter);
        mImgPhoto1.setOnClickListener(this);
        mImgPhoto2.setOnClickListener(this);
        mImgPhoto3.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.radio_man:
                mRadioMan.setTextColor(Color.WHITE);
                mRadioWoman.setTextColor(getResources().getColor(R.color.color_unselected));
                mRadioOther.setTextColor(getResources().getColor(R.color.color_unselected));
                break;
            case R.id.radio_woman:
                mRadioWoman.setTextColor(Color.WHITE);
                mRadioMan.setTextColor(getResources().getColor(R.color.color_unselected));
                mRadioOther.setTextColor(getResources().getColor(R.color.color_unselected));
                break;
            case R.id.radio_other:
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
