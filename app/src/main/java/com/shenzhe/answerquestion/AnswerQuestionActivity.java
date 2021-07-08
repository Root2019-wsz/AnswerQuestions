package com.shenzhe.answerquestion;

import android.content.Context;
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

import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.ImageUtil;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.SoftKeyBoardListener;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.io.FileNotFoundException;

public class AnswerQuestionActivity extends BaseActivity{

    private EditText mContent;
    private Question question;
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
        setContentView(R.layout.activity_answer_question);

        //接收传递的Question对象
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("question_data");
        setUpViews();
    }

    private void setUpViews() {
        //初始化ToolBar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("回答:" + question.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        mCloseKeyboard = findViewById(R.id.bt_down);
        mTakePhoto = findViewById(R.id.bt_take_photo);
        mOpenAlbum = findViewById(R.id.bt_open_album);
        mCancelImage = findViewById(R.id.cancel_image);
        mAllImage = findViewById(R.id.answer_image_all);
        mAnswerImage = findViewById(R.id.answer_image);

        SoftKeyBoardListener.setListener(AnswerQuestionActivity.this,
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

        // 关闭键盘
        mCloseKeyboard.setOnClickListener(v -> hideKeyboard());

        mCancelImage.setOnClickListener(v -> {
            hasImage = false;
            mAllImage.setVisibility(View.GONE);
        });

        mCancelImage.setOnClickListener(v -> {
            hasImage = false;
            mAllImage.setVisibility(View.GONE);
        });
        mTakePhoto.setOnClickListener(v -> checkCameraPermission());
        mOpenAlbum.setOnClickListener(v -> checkAlbumPermission());


        //初始化输入框 并设置监听器
        //当输入框类输入文字时,
        //把发送按钮由黑色变成蓝色,提升视觉效果(参考知乎)
        mContent = findViewById(R.id.text_answer_content);

        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !MyTextUtils.isEmpty(mContent.getText().toString());
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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
                finish();
                break;
            case R.id.send:
                Log.d("BUsend", "onOptionsItemSelected: send");
                if(hasImage) uploadImageAndSend();
                else send();
                break;
            case R.id.send_pre:
                Log.d("BUsend", "onOptionsItemSelected: send_pre");
                if(hasImage) uploadImageAndSend();
                else send();
                break;
        }
        return true;
    }

    private void uploadImageAndSend() {
        ToastUtil.makeToast("正在上传图片...");
        HttpUtil.uploadToQINiu(imagePath, imageName, (key, info, response) -> {
            if (info.isOK()) {
                send();
            }else {
                ToastUtil.makeToast("上传失败,请稍后再试");
            }
        });
    }

    private void send() {

        Log.d("回答问题", "回答一次");
        //内容不为空时 发送
        if (!MyTextUtils.isEmpty(mContent.getText().toString())) {
            String param;
            if (hasImage) {
                param = "qid=" + question.getId()
                        + "&content=" + mContent.getText().toString() + "&images="
                        + ApiParam.MY_QINIU_URL + imageName + "&uid="+MyApplication.getId();
            } else param = "qid=" + question.getId()
                    + "&content=" + mContent.getText().toString() + "&uid="+MyApplication.getId();

            HttpUtil.sendHttpRequest(ApiParam.ANSWER_A_QUESTION, param,
                    new HttpUtil.HttpCallBack() {
                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getInfo().equals("success")) {
                                ToastUtil.makeToast("回答成功!");
                                finish();
                            } else {
                                ToastUtil.makeToast(response.getInfo());
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            if ("参数".equals(reason))
                                ToastUtil.makeToast("由于网络原因,回答失败");
                        }
                    });
        } else if (MyTextUtils.isEmpty(mContent.getText().toString())) {
            ToastUtil.makeToast("内容不能为空");
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
                        mAnswerImage.setImageBitmap(Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true));
                        mAllImage.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    hasImage = false;
                    mAllImage.setVisibility(View.GONE);
                    ToastUtil.makeToast("未能成功获得图片,请稍后再试!");
                }
                break;
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    hasImage = true;
                    imageName = MyApplication.getUser().getUsername() + "questionImage"
                            +".jpg";
                    imagePath = ImageUtil.parseImageUri(data);
                    if (imagePath != null) {
                        imageBitmap = BitmapFactory.decodeFile(imagePath);
                        mAnswerImage.setImageBitmap(imageBitmap);
                        mAllImage.setVisibility(View.VISIBLE);
                    } else {
                        hasImage = false;
                        mAllImage.setVisibility(View.GONE);
                        ToastUtil.makeToast("未能成功获得图片,请稍后再试!");
                    }
                }
        }
    }

    public static void actionStart(Context context, Question question) {
        Intent intent = new Intent(context, AnswerQuestionActivity.class);
        intent.putExtra("question_data", question);
        context.startActivity(intent);
    }
}