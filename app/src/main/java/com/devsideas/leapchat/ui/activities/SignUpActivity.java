package com.devsideas.leapchat.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.devsideas.leapchat.R;
import com.devsideas.leapchat.util.AppConstants;
import com.devsideas.leapchat.util.ImageLoader;
import com.devsideas.leapchat.util.IntentHelper;
import com.devsideas.leapchat.util.SharedPreferenceHelper;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.ConfirmationCodeCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;

public class SignUpActivity extends ImageGalleryActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ri1Tekkh5lrSwABgGafCGVOdO";
    private static final String TWITTER_SECRET = "QJ8Kbqd32NRwgywtRNm0wzrkSOAfAHFQEgFQPpTboxlH24disA";
    private static final String TAG = "Signup";
    @Inject
    ImageLoader imageLoader;
    @BindView(R.id.txt_name)
    EditText mTxtName;
    @BindView(R.id.radioGroup_genre)
    RadioGroup mRadioGroup;
    @BindView(R.id.radio_man)
    RadioButton mRadioMan;
    @BindView(R.id.radio_other)
    RadioButton mRadioOther;
    @BindView(R.id.radio_woman)
    RadioButton mRadioWoman;
    @BindView(R.id.toolbar_signup)
    Toolbar mActionBar;
    @BindView(R.id.spin_interested)
    BetterSpinner mSpin;
    @BindView(R.id.img_photo1)
    CircleImageView mImgPhoto1;
    private String mGenre = "";
    private String mInterested = "";
    private AuthCallback authCallback;
    private boolean loadedPic;
    private Bitmap picture;
    SweetAlertDialog pDialog;

    private static final String[] INTERESTED = new String[]{
            "Hombres", "Mujeres", "Ambos"
    };
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        mAuth = FirebaseAuth.getInstance();
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {
                // Do something with the session
                showDialog();
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.Phone, phoneNumber);
                mAuth.createUserWithEmailAndPassword(SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone) + "@leapchat.com", phoneNumber).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mAuth.signInWithEmailAndPassword(SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone) + "@leapchat.com", SharedPreferenceHelper.loadString(SharedPreferenceHelper.Phone)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    pDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Auth Failed",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    savePicture(phoneNumber);
                                    SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.ACTIVE, true);
                                }
                            }
                        });

                    }
                });
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
            }
        };
        ButterKnife.bind(this);
        mImgPhoto1.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, INTERESTED);
        mSpin.setAdapter(adapter);
    }

    @OnClick(R.id.lab_signup)
    void signUp() {
        if (!mTxtName.getText().toString().equals("") && mTxtName.getText().toString().length() > 4) {
            if (!mGenre.equals("")) {
                if (loadedPic) {
                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.Name, mTxtName.getText().toString());
                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.Genre, mGenre);
                    AuthConfig.Builder digitsAuthConfigBuilder = new AuthConfig.Builder()
                            .withAuthCallBack(authCallback);
                    Digits.authenticate(digitsAuthConfigBuilder.build());
                } else
                    Toasty.warning(this, getString(R.string.miss_pictures), Toast.LENGTH_SHORT, true).show();
            } else
                Toasty.warning(this, getString(R.string.miss_genre), Toast.LENGTH_SHORT, true).show();
        } else
            Toasty.warning(this, getString(R.string.miss_name), Toast.LENGTH_SHORT, true).show();
    }

    private void showDialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Registrando Usuario");
        pDialog.setCancelable(false);
        pDialog.show();
    }

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
                loadedPic = true;
                showDialogPicturesOptions(R.id.img_photo1, "pro_pic");
                break;
        }
    }

    private void savePicture(String phone) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://leapchat-160802.appspot.com/profile_picture");
        StorageReference mountainsRef = storageRef.child(phone + ".jpg");
        StorageReference mountainImagesRef = storageRef.child("images/" + phone + ".jpg");
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.MY_PIC, downloadUrl.toString());
                pDialog.dismiss();
                IntentHelper.goToHome(SignUpActivity.this);
            }
        });
    }


    public Bitmap createFile(Uri uri, File file) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    File file = new File(getCacheDir(), "pro_pic.jpg");
                    final int THUMBSIZE = 128;
                    picture = ThumbnailUtils.extractThumbnail(createFile(data.getData(), file), THUMBSIZE, THUMBSIZE);
                    mImgPhoto1.setImageBitmap(picture);
                }

                break;
            case PHOTO_CODE:
                final String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "pro_pic";
                Bitmap bitmapImage = BitmapFactory.decodeFile(path + ".jpg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                picture = Bitmap.createScaledBitmap(bitmapImage, 150, 150, true);
                mImgPhoto1.setImageBitmap(picture);
                break;
        }
    }
}
