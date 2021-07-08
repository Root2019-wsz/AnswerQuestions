package com.shenzhe.answerquestion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.io.File;
import java.io.IOException;

public class BaseActivity extends AppCompatActivity{

    final int TAKE_PHOTO = 3;
    final int OPEN_ALBUM = 4;

    String imageName;
    Uri imageUri;

    void checkAlbumPermission() {
        if (ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, OPEN_ALBUM);
        } else {
            openAlbum();
        }
    }

    void checkCameraPermission() {
        //判断相机权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
            } else {
                takePhoto();
            }
        } else {
            takePhoto();
        }
    }

    void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_ALBUM);
    }

    void takePhoto() {
        imageName = MyApplication.getUser().getUsername() + "avatar" + ".jpg";
        File outputImage = new File(getExternalCacheDir(), imageName);
        if (outputImage.exists())
            outputImage.delete();
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(BaseActivity.this,
                    "com.shenzhe.AnswerQuestion.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.
                getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(BaseActivity.this.getWindow().
                    getDecorView().getWindowToken(), 0);
        }

    }

    int getExifOrientation(String filepath) {
        int degree = 0;
        androidx.exifinterface.media.ExifInterface exif = null;

        try {
            exif = new androidx.exifinterface.media.ExifInterface(filepath);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }
        return degree;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    ToastUtil.makeToast("你拒绝了权限请求,该功能无法使用!");
                }
                break;
            case OPEN_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    ToastUtil.makeToast("你拒绝了权限请求,该功能无法使用!");
                }
                break;
        }
    }
}