package com.shenzhe.answerquestion.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * 封装图片处理方法
 */

public class ImageUtil {

    public static Bitmap arraryToBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }

    public static String parseImageUri(Intent data){
        if(Build.VERSION.SDK_INT >= 19){
            return handleImageOnKitHat(data);
        }else {
            return handleImageBeforeKitHat(data);
        }
    }
    private static String handleImageBeforeKitHat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }
    @TargetApi(19)
    private static String handleImageOnKitHat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(MyApplication.getContext(), uri)) {
            //如果是Document类型的uri,通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads")
                        , Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri 用普通方式的处理
            imagePath = uri.getPath();
        }
        return imagePath;
    }


    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = MyApplication.getContext().getContentResolver().
                query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
