package com.shenzhe.answerquestion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shenzhe.answerquestion.bean.Person;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.ToastUtil;

public class LoginActivity extends AppCompatActivity{

    private TextInputEditText mAccount;
    private TextInputEditText mPassword;
    private TextInputLayout passwordLayout;
    private TextInputLayout accountLayout;
    private CheckBox mRememberPassword;
    private CheckBox mAutoLogin;
    private SharedPreferences pref;
    private TextView toSignUp;
    private boolean autoLogin;
    private boolean isRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpViews();

        setRememberAndAutoLogin();
        if (autoLogin) {
            autoLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRememberAndAutoLogin();
    }

    private void setUpViews() {
        mAccount = findViewById(R.id.input_account);
        mPassword = findViewById(R.id.input_password);
        mRememberPassword = findViewById(R.id.checkbox_remember_password);
        mAutoLogin = findViewById(R.id.checkbox_auto_login);
        Button loginButton = findViewById(R.id.button_login);
        accountLayout = findViewById(R.id.login_account_layout);
        passwordLayout = findViewById(R.id.login_password_layout);
        toSignUp = findViewById(R.id.to_sign_up);


        //设置自动登陆 保存密码


        initListener();

        loginButton.setOnClickListener(v -> {
            logIn();
//            loginButton.setClickable(false);
        });


    }

    private void initListener() {

        mAutoLogin.setOnClickListener(v -> {
            if (mAutoLogin.isChecked() && !mRememberPassword.isChecked()) {
                mRememberPassword.setChecked(true);
            }
        });
        mRememberPassword.setOnClickListener(v -> {
            Log.d("autorem", "AutoLogin.isChecked = " + mAutoLogin.isChecked()
                    + "   mRememberPassword.isChecked = " + mRememberPassword.isChecked());
            if (mAutoLogin.isChecked() && !mRememberPassword.isChecked()) {
                mAutoLogin.setChecked(false);
            }
        });

        toSignUp.setOnClickListener(v -> {
            SignUpActivity.actionStart(LoginActivity.this);
            mAutoLogin.setChecked(false);
        });
    }

    private void setRememberAndAutoLogin() {
        pref = getSharedPreferences("account", Context.MODE_PRIVATE);
        isRemember = pref.getBoolean("remember_password", false);
        autoLogin = pref.getBoolean("auto_login", false);
        if (isRemember) {
            mAccount.setText(pref.getString("account", ""));
            mPassword.setText(pref.getString("password", ""));
            mRememberPassword.setChecked(true);
        }
    }


    private void autoLogin() {

        mAutoLogin.setChecked(true);
        ToastUtil.makeToast("1秒后将自动登录,现在取消还来得及哦!");
        new Thread(() -> {
            try {
                Thread.sleep(1200);
                runOnUiThread(() -> {
                    if (mAutoLogin.isChecked()) {
                        logIn();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveRememberAndAutoLogin() {
        //检测是否记住密码,自动登陆 并保存
        SharedPreferences.Editor editor = pref.edit();
        if (mRememberPassword.isChecked()) {
            editor.putBoolean("remember_password", true);
            editor.putString("account", mAccount.getText().toString());
            editor.putString("password", mPassword.getText().toString());
//            editor.putInt("sessionId",MyApplication.getUser().getId());
            if (mAutoLogin.isChecked()) {
                editor.putBoolean("auto_login", true);
            }else editor.putBoolean("auto_login", false);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void logIn() {
        if (MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10))
            accountLayout.setErrorEnabled(false);


        if (MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18))
            passwordLayout.setErrorEnabled(false);


        if (MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10)
                && MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18)) {
            accountLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            String username = mAccount.getText().toString();
            String password = mPassword.getText().toString();
            //实现登录逻辑
            String param = "username=" + username + "&password=" + password;
            HttpUtil.sendHttpRequest(ApiParam.LOGIN, param,
                    new HttpUtil.HttpCallBack() {
                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getInfo().equals("success")) {
                                //设置全局用户
                                MyApplication.setUser(JsonParse.getUser(response.getData()));
//                                Log.d("TestId",""+ MyApplication.getId());
                                //保存账号密码
                                saveRememberAndAutoLogin();
                                //跳转到MainActivity
                                QuestionListActivity.actionStart(LoginActivity.this);
                                ToastUtil.makeToast("欢迎来到邮问有答!");
                                finish();
                            } else {
                                ToastUtil.makeToast(response.getInfo());
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            if ("参数".equals(reason)) {
                                ToastUtil.makeToast("账号密码有误,登录失败");
                            }
                        }
                    });

        } else {
            if (!MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10)) {
                accountLayout.setErrorEnabled(true);
                accountLayout.setError("用户名不合法");
            }
            if (!MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18)) {
                passwordLayout.setErrorEnabled(true);
                passwordLayout.setError("密码不合法");
            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}