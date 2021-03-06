package com.shenzhe.answerquestion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.ImageUtil;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.SoftKeyBoardListener;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.io.FileNotFoundException;

public class AskQuestionActivity extends BaseActivity{

    private TextInputEditText mTitle;
    private EditText mContent;
    private TextInputLayout mTittleLayout;

    private Button mCloseKeyboard;
    private Button mTakePhoto;
    private ImageView mCancelImage;
    private Button mOpenAlbum;
    private ImageView mAnswerImage;
    private FrameLayout mAllImage;

    private Bitmap imageBitmap;
    private String imagePath;

    private boolean hasImage = false;
    private boolean mIsEditStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setUpViews();
    }

    private void setUpViews(){
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        //??????????????????
        mTittleLayout = findViewById(R.id.text_input_layout);
        mTitle = findViewById(R.id.text_question_title);
        mContent = findViewById(R.id.text_question_content);

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
                if (MyTextUtils.isEmpty(mTitle.getText().toString())) {
                    mTittleLayout.setErrorEnabled(true);
                    mTittleLayout.setError("??????????????????");
                } else {
                    mTittleLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCloseKeyboard = findViewById(R.id.bt_down);
        mTakePhoto = findViewById(R.id.bt_take_photo);
        mOpenAlbum = findViewById(R.id.bt_open_album);
        mCancelImage = findViewById(R.id.cancel_image);
        mAllImage = findViewById(R.id.question_image_all);
        mAnswerImage = findViewById(R.id.question_image);


        SoftKeyBoardListener.setListener(AskQuestionActivity.this,
                new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                    @Override
                    public void keyBoardShow(int height) {
                        mCloseKeyboard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void keyBoardHide(int height) {
                        mCloseKeyboard.setVisibility(View.GONE);
                    }
                });

        // ????????????
        mCloseKeyboard.setOnClickListener(v -> hideKeyboard());

        mCancelImage.setOnClickListener(v -> {
            hasImage = false;
            mAllImage.setVisibility(View.GONE);
        });
        mTakePhoto.setOnClickListener(v -> checkCameraPermission());
        mOpenAlbum.setOnClickListener(v -> checkAlbumPermission());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsEditStatus) {
            menu.findItem(R.id.send).setVisible(false);
            menu.findItem(R.id.send_pre).setVisible(true);
        } else {
            menu.findItem(R.id.send).setVisible(true);
            menu.findItem(R.id.send_pre).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.send:
                if (hasImage) uploadImageAndSend();
                else send();
                break;
            case R.id.send_pre:
                if (hasImage) uploadImageAndSend();
                else send();
                break;
        }
        return true;
    }

    private void uploadImageAndSend() {
        ToastUtil.makeToast("??????????????????...");
        HttpUtil.uploadToQINiu(imagePath, imageName, (key, info, response) -> {
            if (info.isOK()) {
                send();
            } else {
                ToastUtil.makeToast("????????????,???????????????");
            }
        });
    }

    private void send() {
        //?????????????????????????????? ??????
        if (!MyTextUtils.isEmpty(mTitle.getText().toString()) &&
                !MyTextUtils.isEmpty(mContent.getText().toString())) {
            mTittleLayout.setErrorEnabled(false);
            String param;
            if (hasImage) {
                param = "title=" + mTitle.getText().toString() +
                        "&content=" + mContent.getText().toString() + "&images="
                        + ApiParam.MY_QINIU_URL + imageName +"&uid="+MyApplication.getId();
            } else
                param = "title=" + mTitle.getText().toString()
                    + "&content=" + mContent.getText().toString() + "&uid="+MyApplication.getId();
            HttpUtil.sendHttpRequest(ApiParam.ASK_A_QUESTION, param, new HttpUtil.HttpCallBack() {
                @Override
                public void onResponse(HttpUtil.Response response) {
                    if (response.getInfo().equals("success")) {
                        ToastUtil.makeToast("????????????");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtil.makeToast(response.getInfo());
                    }
                }

                @Override
                public void onFail(String reason) {
                    ToastUtil.makeToast("??????????????????,????????????");
                }
            });
        } else {
            if (MyTextUtils.isEmpty(mTitle.getText().toString())) {
                mTittleLayout.setErrorEnabled(true);
                mTittleLayout.setError("??????????????????!");
            }


            if (MyTextUtils.isEmpty(mContent.getText().toString()))
                ToastUtil.makeToast("??????????????????");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        hasImage = true;
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        imagePath = getExternalCacheDir().getPath() + "/" + imageName;
                        Log.d("TAKEPHOTO", "path=m" + imagePath);
                        int degree = getExifOrientation(imagePath);
                        Log.d("DEGREE", String.valueOf(degree));
                        // preview icon according to exif orientation
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        mAnswerImage.setMaxHeight(80);
                        mAnswerImage.setImageBitmap(Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true));
                        mAllImage.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    hasImage = false;
                    mAllImage.setVisibility(View.GONE);
                    ToastUtil.makeToast("????????????????????????,???????????????!");
                }
                break;
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    imageName = MyApplication.getUser().getUsername() + "questionImage" + ".jpg";
                    imagePath = ImageUtil.parseImageUri(data);
                    Log.d("imagePath", "imagePath: " + imagePath);

                    if (imagePath != null) {
                        hasImage = true;
                        imageBitmap = BitmapFactory.decodeFile(imagePath);
                        mAnswerImage.setImageBitmap(imageBitmap);
                        mAllImage.setVisibility(View.VISIBLE);
                    } else {
                        hasImage = false;
                        mAllImage.setVisibility(View.GONE);
                        ToastUtil.makeToast("????????????????????????,???????????????!");
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}