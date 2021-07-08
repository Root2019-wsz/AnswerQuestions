package com.shenzhe.answerquestion.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import android.os.Handler;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.StringUtils;

import org.apache.http.impl.client.DefaultHttpClient;

import okhttp3.Cookie;
import okhttp3.Response;

/**
 * Http通信类，封装网络请求
 */

@SuppressLint("NewApi")
public class HttpUtil {
    private  static int sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
    private static  final String TAG = "HttpUtil";
    private static boolean flag = false;
    private static Handler handler;
    private static boolean networkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)MyApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (sdkVersion <= Build.VERSION_CODES.M){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if ((info != null && info.isAvailable())){
                flag = true;
            }
        }else if(sdkVersion > Build.VERSION_CODES.M){
            Network network = connectivityManager.getActiveNetwork();
            if (network == null){
                ToastUtil.makeToast("unavailable");
                flag = false;
            }else {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                assert networkCapabilities != null;
//                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
//                    ToastUtil.makeToast("Cellular");
//                }
//                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
//                    ToastUtil.makeToast("Wifi");
//                }
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static void loadImage(String address,ImageCallback callback){
        String imageName;
        Log.d(TAG, "原address=" + address);
        String[] names = address.split("com/");
        imageName = names[names.length - 1];
        if (imageName.contains("/"))
            imageName = imageName.replaceAll("/", "");

        Log.d(TAG,"文件名="+imageName);
        File file = new File(MyApplication.getContext().getExternalCacheDir().getPath() + "/" + imageName + ".png");
        if (file.exists()) {
            //文件存在则从文件中读取
            try {
                Log.d(TAG, "文件存在!!    name=" + imageName + "\n" + "path=" + file.getPath() + "\n" + file.getAbsolutePath() +
                        "\n" + file.getCanonicalPath());
                callback.onResponse(BitmapFactory.decodeFile(file.getPath()), "success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "文件不存在  name=" + imageName);
            if (networkAvailable()){
                String finalImageName = imageName;
                new Thread(() ->{
                    HttpURLConnection connection;
                    try {
                        URL murl = new URL(address);
                        connection = (HttpURLConnection)murl.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(5000);
                        connection.setConnectTimeout(8000);
                        connection.setDoInput(true);
                        connection.setUseCaches(false);//不缓存
                        connection.connect();

                        Log.d(TAG, "ResponseCode" + connection.getResponseCode());

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                            handler.post(() -> callback.onResponse(bitmap, "success"));
                            //缓存图片
                            File file1 = new File(MyApplication.getContext().getExternalCacheDir() + "/" + finalImageName + ".png");
                            FileOutputStream os = new FileOutputStream(file1);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 70, os);
                        } else {
                            handler.post(() ->callback.onResponse(null,"failed"));
                        }
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }catch (ProtocolException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }).start();
            }else {
                handler.post(() ->{
                    callback.onResponse(null,"failed");
                    ToastUtil.makeToast("网络不可用,请检查网络连接后再试");
                });
            }
        }
    }

    public static void uploadToQINiu(String filePath, String name, UpCompletionHandler upCompletionHandler){
        Configuration config = new Configuration.Builder().zone(FixedZone.zone0).build();
        UploadManager uploadManager = new UploadManager(config);

        if (networkAvailable()){
            String param = "accessKey=2OJqT3fDjotWQQa9QddYqTvAmSdfY08nfDF6fZMU"
                    + "&secretKey=DnIeM4yEO455rz71Fq4H4shzYRohWQEtW1Iu3o-0"
                    + "&bucket=wsz12123";
            sendHttpRequest(ApiParam.GET_TOKEN, param, new HttpCallBack() {
                @Override
                public void onResponse(Response response) {
                    if (response.getInfo().equals("success")) {
                        String token = JsonParse.getElement(new String(response.getBytes()), "token");
                        uploadManager.put(filePath, name, token, upCompletionHandler, null);
                    } else {
                        ToastUtil.makeToast(response.getInfo());
                    }
                }

                @Override
                public void onFail(String reason) {
                    ToastUtil.makeToast("获取七牛token失败");
                }
            });
        }else {
            handler.post(() -> ToastUtil.makeToast("网络不可用,请检查网络连接后再试"));
        }
    }

    public static void sendHttpRequest(String url, String param, HttpCallBack callBack) {
        handler = new Handler();
        if (networkAvailable()){
            //把开启新线程的操作封装在网络请求里
            new Thread(() -> {
                HttpURLConnection connection;
                Log.d(TAG, "网络请求");
                try {
                    URL mUrl = new URL(url);
                    connection = (HttpURLConnection) mUrl.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(8000);
                    OutputStream os = connection.getOutputStream();
                    if (param != null){
                        os.write(param.getBytes());
                    }
                    os.flush();
                    os.close();
                    int responseCode = connection.getResponseCode();
                    Log.d("ResponseCode", "ResponseCode = " + responseCode);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Response response = new Response(getByteArrayFromIS(connection.getInputStream()));
                        if(response.getInfo()!=null){
                            Log.d(TAG, "info = " + response.getInfo());
                            handler.post(() -> callBack.onResponse(response));
                        }
                    } else {
                        handler.post(() -> {
                            ToastUtil.makeToast("服务器连接错误 responseCode = " + responseCode);
                            callBack.onFail("网络");
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            callBack.onFail("网络");
            handler.post(() -> ToastUtil.makeToast("网络不可用,请检查网络连接后再试"));
        }
    }

    private static byte[] getByteArrayFromIS(InputStream is) throws IOException {
        byte buff[] = new byte[1024];
        int len;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while ((len = is.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        byte[] bytes = os.toByteArray();
        is.close();
        os.close();
        return bytes;
    }

    public static class Response {
        private int status;
        private String info;
        private String data;
        private byte[] bytes;

        public Response(byte[] content) {
            bytes = content;
            String contentString = new String(content);

//            if (JsonParse.getElement(contentString, "status") != null) {
//                status = Integer.parseInt(JsonParse.getElement(contentString, "status"));
//            } else {
//                status = 200;
//            }
                if (JsonParse.getElement(contentString,"data")!=null) {
                    info = JsonParse.getElement(contentString, "info");
                    data = JsonParse.getElement(contentString, "data");
                }
        }

        public int getStatus() {
            return status;
        }

        public String getInfo() {
            return info;
        }

        public String getData() {
            return data;
        }


        public byte[] getBytes() {
            return bytes;
        }
    }

    public interface HttpCallBack {
        void onResponse(Response response);

        void onFail(String reason);
    }

    public interface ImageCallback {
        void onResponse(Bitmap bitmap, String info);
    }

}
