package com.shenzhe.answerquestion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shenzhe.answerquestion.adapter.AnswerListRvAdapter;
import com.shenzhe.answerquestion.adapter.QuestionListRvAdapter;
import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListActivity extends AppCompatActivity{

    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private QuestionListRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);
        setUpViews();
    }

    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteListActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList,QuestionListRvAdapter.TYPE_FAVORITE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        initSwipeRefresh();
    }

    //刷新问题列表数据
    private void updateQuestions() {
        AnswerListRvAdapter.needToLoad = true;
        swipeRefresh.setRefreshing(true);
        //数据请求
        HttpUtil.sendHttpRequest(ApiParam.GET_FAVORITE_LIST, "uid="+MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")){
                            mQuestionList.clear();
                            //Log.d(TAG, "onSuccess: " + data);
                            mQuestionList.addAll(JsonParse.getQuestionList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }else{
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


    private void initSwipeRefresh() {
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateQuestions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateQuestions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, FavoriteListActivity.class);
        context.startActivity(intent);
    }
}