package com.saild.cropdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.saild.croplibrary.photo.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_PICK_CAMERA = 101;
    private static final int REQUEST_PICK_STORAGE = 102;
    private Button mTakeBT, mPickImageBT;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        //适配7.0手机拍照
        Crop.setAppPacket(getPackageName());
        mTakeBT = findViewById(R.id.bt_take_photo);
        mPickImageBT = findViewById(R.id.bt_pickImage);
        mImageView = findViewById(R.id.iv_image);
        mTakeBT.setOnClickListener(this);
        mPickImageBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_take_photo:
                requestCampraPermission();
                break;
            case R.id.bt_pickImage:
                requestStoragePermission();
                break;
        }
    }

    //处理6.0动态权限问题
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PICK_STORAGE);
        } else {
            pickImage();
        }
    }

    private void requestCampraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_PICK_CAMERA);
        } else {
            cropFromCamera();
        }
    }

    private void pickImage() {
        Crop.pickImage(this);
    }

    private void cropFromCamera() {
        Crop.takePhoto(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            beginCrop(Crop.getOutputFileUri());
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            Crop.clearCacheFile();
            handleCrop(resultCode, data);
        }
    }

    public void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        Crop.of(source, destination).withMaxSize(1242, 1242).asSquare().start(this);
    }

    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            mImageView.setImageURI(uri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, "result_error_v1.3", Toast.LENGTH_SHORT).show();
        }
    }
}
