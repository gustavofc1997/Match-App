package com.devsideas.leapchat.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.devsideas.leapchat.util.AppConstants;

import java.io.File;

/**
 * Created by tavit on 2/19/2017.
 */

public class ImageGalleryActivity extends AppCompatActivity {
    private String APP_DIRECTORY = "App/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_STORAGE_VIDEO = 2;
    private static final int REQUEST_CAMERA = 0;
    public final int SELECT_PICTURE = 200;
    public final int PHOTO_CODE = 100;
    private CharSequence[] options = {AppConstants.TAKE_PICTURE, AppConstants.CHOOSE_GALLERY, AppConstants.DELETE};
    private String FileName = "";
    private boolean isVideo;

    public void checkCameraPermission(String fileName) {
        FileName=fileName;
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ImageGalleryActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else if (result == PackageManager.PERMISSION_GRANTED) {
            checkStoragePermission(-1, REQUEST_CAMERA, fileName);
        }
    }


    public void showDialogPicturesOptions(final int viewId, final String fileName) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ImageGalleryActivity.this);
        alertBuilder.setTitle("");
        alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(AppConstants.TAKE_PICTURE)) {
                    checkCameraPermission(fileName);
                } else if (options[which].equals(AppConstants.CHOOSE_GALLERY)) {
                    isVideo = false;
                    checkStoragePermission(viewId, REQUEST_STORAGE, fileName);
                }

            }
        });
        alertBuilder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CAMERA){
            checkStoragePermission(-1, REQUEST_CAMERA, FileName);
        }
    }

    public void checkStoragePermission(int imageId, int requestCode, String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ImageGalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            } else if (result == PackageManager.PERMISSION_GRANTED) {
                verifyRequestCode(imageId, requestCode, fileName);
            }
        } else {
            verifyRequestCode(imageId, requestCode, fileName);
        }
    }

    public void openGallery(int imageId) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        Bundle extras = new Bundle();
        extras.putInt("imageId", imageId);
        intent.putExtras(extras);
        startActivityForResult(intent.createChooser(intent, "Selecciona una foto existente"), SELECT_PICTURE);

    }

    public void openCamera(String fileName) {

//       / File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
//        file.mkdirs();
//        String path = Environment.getExternalStorageDirectory() + File.separator
//                + Environment.DIRECTORY_PICTURES + File.separator + FileName;
//        File newFile = new File(path);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        Uri photoURI = FileProvider.getUriForFile(CreateCampaignActivity.this, getApplicationContext().getPackageName() + ".provider", newFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//        startActivityForResult(intent, PHOTO_CODE);
//         File image = File.createTempFile(
//                FileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();
        String path = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator + fileName + ".jpg";
        File newFile = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }


    public void verifyRequestCode(int imageId, int requestCode, String fileName) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                openGallery(imageId);
                break;
            case REQUEST_CAMERA:
                openCamera(fileName);
                break;
        }
    }

}
