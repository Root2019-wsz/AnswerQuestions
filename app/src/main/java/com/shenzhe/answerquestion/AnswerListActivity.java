package com.shenzhe.answerquestion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shenzhe.answerquestion.adapter.AnswerListRvAdapter;
import com.shenzhe.answerquestion.bean.Answer;
import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AnswerListActivity extends AppCompatActivity{

    private Question mQuestion;

    private List<Answer> mAnswerList = new ArrayList<>();
    private AnswerListRvAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        Intent intent = getIntent();
        mQuestion = (Question) intent.getSerializableExtra("question_data");
        setUpViews();
        updateAnswers();
    }

    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(mQuestion.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerListActivity.this);
        adapter = new AnswerListRvAdapter(mAnswerList, mQuestion);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //设置悬浮按钮
        FloatingActionButton button = findViewById(R.id.fab_ask);
        button.setOnClickListener(v -> AnswerQuestionActivity
                .actionStart(AnswerListActivity.this, mQuestion));

        //设置下拉刷新
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateAnswers);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAnswers();
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

    private void updateAnswers() {
        //刷新后,允许重新加载
        AnswerListRvAdapter.needToLoad = true;
        swipeRefresh.setRefreshing(true);
        //需要改
        HttpUtil.sendHttpRequest(ApiParam.GET_ANSWER_LIST,  "qid=" + mQuestion.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            mAnswerList.clear();//数据请求
                            mAnswerList.addAll(JsonParse.getAnswerList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        } else {
                            swipeRefresh.setRefreshing(false);
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

    public static void actionStart(Context context, Question question) {
        Intent intent = new Intent(context, AnswerListActivity.class);
        intent.putExtra("question_data", question);
        context.startActivity(intent);
    }
}

