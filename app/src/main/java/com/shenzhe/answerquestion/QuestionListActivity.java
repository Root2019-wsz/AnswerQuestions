package com.shenzhe.answerquestion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shenzhe.answerquestion.adapter.QuestionListRvAdapter;
import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.ToastUtil;
import com.shenzhe.answerquestion.view.MyDialog;
import com.shenzhe.answerquestion.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

public class QuestionListActivity extends AppCompatActivity{

    private boolean needRefresh = false;

    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private QuestionListRvAdapter adapter;
    private MyDialog dialog;
    private RoundImageView avatar;
    private TextView mUsernameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        setUpViews();
        updateQuestions();
    }

    //刷新问题列表数据
    private void updateQuestions() {
        //要改
        swipeRefresh.setRefreshing(true);
        HttpUtil.sendHttpRequest(ApiParam.GET_QUESTION_LIST, null,
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            mQuestionList.clear();//数据请求
                            //Log.d(TAG, "onSuccess: " + data);
                            mQuestionList.addAll(JsonParse.getQuestionList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("网络错误,刷新失败");
                        swipeRefresh.setRefreshing(false);
                    }
                });
    }


    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        //设置NavigationView

        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        mUsernameTv = view.findViewById(R.id.tv_nav_header_username);
        avatar = view.findViewById(R.id.nav_header_avatar);


        mUsernameTv.setText(MyApplication.getUser().getUsername());
        if (!MyTextUtils.isNull(MyApplication.getUser().getAvatar())) {
            HttpUtil.loadImage(MyApplication.getUser().getAvatar(), (bitmap, info) -> {
                if ("success".equals(info)) avatar.setImageBitmap(bitmap);
                else avatar.setImageResource(R.drawable.nav_icon);
            });
        }
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    drawerLayout.closeDrawers();
                    break;
                case R.id.nav_favorite:
                    drawerLayout.closeDrawers();
                    FavoriteListActivity.actionStart(QuestionListActivity.this);
                    break;
                case R.id.nav_avatar:
                    //跳转到修改头像
                    ChangeAvatarActivity.actionStart(QuestionListActivity.this);
                    break;
                case R.id.nav_password:
                    //修改密码
                    setDialog();
                    break;
                case R.id.nav_logout:
                    logout();
                    LoginActivity.actionStart(QuestionListActivity.this);
                    finish();
                    break;

            }
            return true;
        });


        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(QuestionListActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList, QuestionListRvAdapter.TYPE_HOME);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //设置悬浮按钮
        FloatingActionButton button = findViewById(R.id.fab_ask);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionListActivity.this, AskQuestionActivity.class);
            startActivityForResult(intent, 666);
        });


        //设置下拉刷新
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateQuestions);

    }


    private void logout() {
        SharedPreferences pref = getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("remember_password", true);
        editor.putString("account", "");
        editor.putString("password", "");
        editor.putBoolean("auto_login", false);
        editor.apply();
    }

    private void setDialog() {
        drawerLayout.closeDrawers();
        dialog = new MyDialog(QuestionListActivity.this);
        //去掉标题
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setChangePasswordView();
        TextInputEditText first = dialog.getFirstEditText();
        TextInputEditText second = dialog.getSecondEditText();
        TextInputLayout firstLayout = dialog.getFirstLayout();
        TextInputLayout secondLayout = dialog.getSecondLayout();

        dialog.getCancelButton().setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });
        dialog.getYesButton().setOnClickListener(v -> {
            String sFirst = first.getText().toString();
            String sSecond = second.getText().toString();
            if (MyTextUtils.isLegal(sFirst, 6, 18)
                    && MyTextUtils.isLegal(sSecond, 6, 18)
                    && MyTextUtils.isEqual(sFirst, sSecond)) {
                changePassword(sFirst);
            } else {
                if (!MyTextUtils.isLegal(sFirst, 6, 18)) {
                    firstLayout.setErrorEnabled(true);
                    firstLayout.setError("密码不合法");
                } else {
                    firstLayout.setErrorEnabled(false);
                }

                if (!MyTextUtils.isEqual(sFirst, sSecond)) {
                    secondLayout.setErrorEnabled(true);
                    secondLayout.setError("两次密码不相同");
                } else {
                    secondLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void changePassword(String newPassword) {
        //要修改
//        Log.d("abcde",MyApplication.getUser().getUsername());
//        Log.d("abcde",MyApplication.getUser().getPassword());
//        Log.d("abcde","" + MyApplication.getId());
        String param ="username="+MyApplication.getUser().getUsername() +"&password"+MyApplication.getUser().getPassword()
                +"&newPassword=" + newPassword + "&uid="+MyApplication.getId();

        HttpUtil.sendHttpRequest(ApiParam.CHANGE_PASSWORD, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {
//                Log.d("abcde",newPassword);
                if (response.getInfo().equals("success")) {
                    ToastUtil.makeToast("修改成功");
                    dialog.dismiss();
                    SharedPreferences pref = getSharedPreferences("account", Context.MODE_PRIVATE);
                    //保存注册好的账号密码
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", MyApplication.getUser().getUsername());
                    editor.putString("password", "");
                    editor.putBoolean("auto_login", false);
                    editor.apply();
                    LoginActivity.actionStart(QuestionListActivity.this);
                    finish();
                }
            }

            @Override
            public void onFail(String reason) {
                ToastUtil.makeToast("修改失败,请稍后再试");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);
        if (needRefresh) updateQuestions();
        if (!MyTextUtils.isNull(MyApplication.getUser().getAvatar())) {
            HttpUtil.loadImage(MyApplication.getUser().getAvatar(), (bitmap, info) -> {
                if ("success".equals(info)) avatar.setImageBitmap(bitmap);
                else avatar.setImageResource(R.drawable.nav_icon);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 666:
                if (resultCode == RESULT_OK) {
                    needRefresh = true;
                } else if (resultCode == RESULT_CANCELED) {
                    needRefresh = false;
                }
                break;
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, QuestionListActivity.class);
        context.startActivity(intent);
    }
}