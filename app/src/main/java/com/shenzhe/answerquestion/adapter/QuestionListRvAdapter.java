package com.shenzhe.answerquestion.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shenzhe.answerquestion.AnswerListActivity;
import com.shenzhe.answerquestion.AnswerQuestionActivity;
import com.shenzhe.answerquestion.R;
import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.DateUtil;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.ToastUtil;
import com.shenzhe.answerquestion.view.RoundImageView;

import java.util.List;

public class QuestionListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "QuestionListRvAdapter";

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_TAIL = 1;
    public static final int TYPE_HOME = 0;
    public static final int TYPE_FAVORITE = 1;

    public static boolean isLoading = false;
    private int questionType;

    private List<Question> mQuestionList;

    public QuestionListRvAdapter(List<Question> mQuestionList, int type) {
        this.mQuestionList = mQuestionList;
        questionType = type;
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        LinearLayout forClick;

        RoundImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;
        ImageView imagePre;


        NormalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_question_title);
            authorName = itemView.findViewById(R.id.tv_question_authorname);
            content = itemView.findViewById(R.id.tv_question_content);
            updateTime = itemView.findViewById(R.id.tv_question_update_time);
            answerNum = itemView.findViewById(R.id.tv_question_answer_num);
            excitingNum = itemView.findViewById(R.id.tv_question_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_question_naive_num);
            date = itemView.findViewById(R.id.tv_ask_date);
            forClick = itemView.findViewById(R.id.layout_to_click);

            avatar = itemView.findViewById(R.id.image_question_avatar);
            comment = itemView.findViewById(R.id.image_question_comment);
            exciting = itemView.findViewById(R.id.image_question_exciting);
            naive = itemView.findViewById(R.id.image_question_naive);
            favorite = itemView.findViewById(R.id.image_question_favorite);
            imagePre = itemView.findViewById(R.id.image_preview);
        }
    }

    static class TailViewHolder extends RecyclerView.ViewHolder {

        private TextView loadingTextView;

        TailViewHolder(View itemView) {
            super(itemView);
            loadingTextView = itemView.findViewById(R.id.tv_loading_tail);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == mQuestionList.size() ? TYPE_TAIL : TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_NORMAL:
                NormalViewHolder holder = new NormalViewHolder(inflater.
                        inflate(R.layout.question_item, parent, false));
                initItemListener(holder);
                return holder;
            case TYPE_TAIL:
                return new TailViewHolder(inflater.inflate(R.layout.tail_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_NORMAL:
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                Question question = mQuestionList.get(position);
                normalViewHolder.title.setText(question.getTitle());
                normalViewHolder.authorName.setText(question.getAuthorName());
                Log.d(TAG, "position: " + position);
                if (!MyTextUtils.isNull(question.getAuthorAvatarUrlString())) {

                    Log.d(TAG, "??? " + position + "?????????" + question.getAuthorName() + "???????????????"
                            + "url=" + question.getAuthorAvatarUrlString());
                    HttpUtil.loadImage(question.getAuthorAvatarUrlString(),
                            (Bitmap bitmap, String info) -> {
                                if ("success".equals(info))
                                    normalViewHolder.avatar.setImageBitmap(bitmap);
                                else normalViewHolder.avatar.setImageResource(R.drawable.nav_icon);
                            });
                }else {
                    Log.d(TAG, "??? " + position + "?????????" + question.getAuthorName() + "????????????");
                    normalViewHolder.avatar.setImageResource(R.drawable.nav_icon);
                }
                if (question.getImages() != null) {
                    Log.d(TAG, "??? " + position + "?????????" + question.getAuthorName() + "???????????????"
                            + "url=" + question.getImages());
                    String url = question.getImages();
                    HttpUtil.loadImage(url, (bitmap, info) -> {
                        if ("success".equals(info)) {
                            normalViewHolder.imagePre.setImageBitmap(bitmap);
                            normalViewHolder.imagePre.setBackgroundResource(R.drawable.bg_dialog);
                            normalViewHolder.imagePre.setVisibility(View.VISIBLE);
                        } else normalViewHolder.imagePre.setVisibility(View.GONE);
                    });
                }else {
                    normalViewHolder.imagePre.setVisibility(View.GONE);
                }

                normalViewHolder.content.setText(question.getContent());
                // Log.d("getRecent", question.getRecent());
                //?????????
                normalViewHolder.date.setText("?????????" + question.getDate());
                if (question.getRecent() != null){
                    normalViewHolder.updateTime.setText(question.getRecent() + "??????");
                }else {
                    normalViewHolder.updateTime.setText("?????????");
                }
                normalViewHolder.answerNum.setText(String.valueOf(question.getAnswerCount()));
                normalViewHolder.excitingNum.setText(String.valueOf(question.getExciting()));
                normalViewHolder.naiveNum.setText(String.valueOf(question.getNaive()));

                if (!question.isExciting()) {
                    normalViewHolder.exciting.setImageResource(R.drawable.ic_exciting);
                } else {
                    normalViewHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                }
                if (!question.isNaive()) {
                    normalViewHolder.naive.setImageResource(R.drawable.ic_naive);
                } else {
                    normalViewHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                }
                if (!question.isFavorite()) {
                    normalViewHolder.favorite.setImageResource(R.drawable.ic_favorite);
                } else {
                    normalViewHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
                }
                break;
                //?????????
            case TYPE_TAIL:
                String param = "uid=" + MyApplication.getId();
                TailViewHolder tailViewHolder = (TailViewHolder) holder;
                String url = (questionType == TYPE_HOME ?
                        ApiParam.GET_QUESTION_LIST : ApiParam.GET_FAVORITE_LIST);
                loadMore(url, param, tailViewHolder);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size() + 1;
    }


//    public boolean isNaive(Question question) {
//        HttpUtil.sendHttpRequest(ApiParam.IS_NATIVE_QUESTION, "qid=" + question.getId() + "&uid="+ MyApplication.getId(),
//                new HttpUtil.HttpCallBack() {
//                    @Override
//                    public void onResponse(HttpUtil.Response response) {
//                        if (response.getInfo().equals("success")) {
//                            question.setNaive(question.getNaive() - 1);
//                            question.setNaive(true);
//                        } else {
//                            ToastUtil.makeToast(response.getInfo());
//                        }
//                    }
//                    @Override
//                    public void onFail(String reason) {
//                        ToastUtil.makeToast("????????????????????????????????????");
//                    }
//                });
//        return question.isNaive();
//    }

//    public boolean isExciting(Question question) {
//        HttpUtil.sendHttpRequest(ApiParam.IS_EXCITING_QUESTION, "qid=" + question.getId() + "&uid="+ MyApplication.getId(),
//                new HttpUtil.HttpCallBack() {
//                    @Override
//                    public void onResponse(HttpUtil.Response response) {
//                        if (response.getInfo().equals("success")) {
//                            question.setExciting(question.getNaive());
//                            question.setExciting(true);
//                        } else {
//                            ToastUtil.makeToast(response.getInfo());
//                        }
//                    }
//                    @Override
//                    public void onFail(String reason) {
//                        ToastUtil.makeToast("????????????????????????????????????");
//                    }
//                });
//        return question.isExciting();
//    }
//
    public boolean isFavorite(Question question) {
        HttpUtil.sendHttpRequest(ApiParam.IS_FAVOURITE, "qid=" + question.getId() + "&uid="+ MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            question.setFavorite(false);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }
                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????1");
                    }
                });
        return question.isFavorite();
    }
//
    private void addExciting(NormalViewHolder holder, Question question){
//        holder.excitingNum.setText(String.valueOf(question.getExciting()));
//        String.valueOf(question.getExciting());
        HttpUtil.sendHttpRequest(ApiParam.ADD_EXCITING_QUESTION, "qid=" + question.getId()+"&uid="+MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
//                            question.setExciting(question.getExciting());
                            holder.excitingNum.setText(response.getData());
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????2");
                    }
                });
    }
//
    private void cancelExciting(NormalViewHolder holder, Question question){
//        holder.excitingNum.setText(String.valueOf(question.getExciting()));
//        holder.exciting.setImageResource(R.drawable.ic_exciting);
        HttpUtil.sendHttpRequest(ApiParam.CANCEL_EXCITING_QUESTION, "qid=" + question.getId()+"&uid="+MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
//                            question.setExciting(question.getExciting());
//                            isExciting(question);
                            holder.excitingNum.setText(response.getData());
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????3");
                    }
                });
    }

    private void addNaive(NormalViewHolder holder, Question question) {

//        holder.naiveNum.setText(String.valueOf(question.getNaive()));
//        holder.naive.setImageResource(R.drawable.ic_naive_clicked);
        HttpUtil.sendHttpRequest(ApiParam.ADD_NATIVE_QUESTION, "qid=" + question.getId()+"&uid="+MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
//                            question.setNaive(question.getNaive());
//                            isNaive(question);
                            holder.naiveNum.setText(response.getData());
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????4");
                    }
                });

    }

    private void cancelNaive(NormalViewHolder holder, Question question) {

//        holder.naiveNum.setText(String.valueOf(question.getNaive()));
//        holder.naive.setImageResource(R.drawable.ic_naive);
        HttpUtil.sendHttpRequest(ApiParam.DELETE_NATIVE_QUESTION, "qid=" + question.getId() + "&uid="+MyApplication.getId(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
//                            question.setNaive(question.getNaive());
//                            isNaive(question);
                            holder.naiveNum.setText(response.getData());
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????5");
                    }
                });

    }

    private void initItemListener(NormalViewHolder holder){
        holder.content.setLines(3);
        holder.forClick.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            AnswerListActivity.actionStart(holder.answerNum.getContext(), question);
        });
        holder.comment.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            Log.d("Intent_que", question.toString());
            AnswerQuestionActivity.actionStart(v.getContext(), question);
        });
        holder.exciting.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            HttpUtil.sendHttpRequest(ApiParam.IS_EXCITING_QUESTION, "qid=" + question.getId() + "&uid="+ MyApplication.getId(),
                    new HttpUtil.HttpCallBack() {
                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getData().equals("false")&&response.getInfo().equals("success")){
                                holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                                question.setExciting(true);
                                addExciting(holder,question);
                            }else {
                                holder.exciting.setImageResource(R.drawable.ic_exciting);
                                question.setExciting(false);
                                cancelExciting(holder,question);
                            }
//                            if (response.getInfo().equals("success")) {
//                                question.setExciting(question.getExciting());
//                                question.setExciting(true);
//                            } else {
//                                ToastUtil.makeToast(response.getInfo());
//                            }
                        }
                        @Override
                        public void onFail(String reason) {
                            ToastUtil.makeToast("????????????????????????????????????6");
                        }
                    });
//            if (!isNaive(question)) {
//                if (!isExciting(question)) {
//                    holder.exciting.setImageResource(R.drawable.ic_exciting);
//                    // ???????????? ?????? ??????????????????+1
//                    addExciting(holder, question);//????????????
//
//                } else {
//                    holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
//                    //????????????  ????????? ??????????????????-1
//                    cancelExciting(holder, question);//????????????
//                }
//            } else {
//                //????????????,?????????
//                cancelNaive(holder, question);
//                addExciting(holder, question);
//            }
        });
        holder.naive.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            HttpUtil.sendHttpRequest(ApiParam.IS_NATIVE_QUESTION, "qid=" + question.getId() + "&uid="+ MyApplication.getId(),
                    new HttpUtil.HttpCallBack() {
                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getData().equals("false")&&response.getInfo().equals("success")){
                                holder.naive.setImageResource(R.drawable.ic_naive_clicked);
                                question.setNaive(true);
                                addNaive(holder,question);
                            }else {
                                holder.naive.setImageResource(R.drawable.ic_naive);
                                question.setNaive(false);
                                cancelNaive(holder,question);
                            }
//                            if (response.getInfo().equals("success")) {
//                                question.setNaive(question.getNaive() - 1);
//                                question.setNaive(true);
//                            } else {
//                                ToastUtil.makeToast(response.getInfo());
//                            }
                        }
                        @Override
                        public void onFail(String reason) {
                            ToastUtil.makeToast("????????????????????????????????????7 ");
                        }
                    });
//            if (!isExciting(question)) {
//                if (!isNaive(question)) {
//                    //????????????  ??? ??????????????????-1
//                    holder.naive.setImageResource(R.drawable.ic_naive);
//                    addNaive(holder, question);
//                } else {
//                    holder.naive.setImageResource(R.drawable.ic_naive_clicked);
//                    //????????????  ??? ??????????????????-1
//                    cancelNaive(holder, question);
//                }
//            } else {
//                //????????????,?????????
//                cancelExciting(holder, question);
//                addNaive(holder, question);
//            }
        });
        holder.favorite.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            holder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
            Log.d("abcde",""+question.getId());
            if (!isFavorite(question)) {
                //????????????  ??????
                HttpUtil.sendHttpRequest(ApiParam.ADD_FAVORITE, "qid=" + question.getId() + "&uid=" + MyApplication.getId(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getInfo().equals("success")) {
                                    isFavorite(question);
                                } else {
                                    ToastUtil.makeToast(response.getInfo());
                                }

                            }

                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("???????????????,?????????????????????");
                            }
                        });
            } else {
                holder.favorite.setImageResource(R.drawable.ic_favorite);
                HttpUtil.sendHttpRequest(ApiParam.CANCEL_FAVORITE, "qid=" + question.getId()
                                + "&uid=" + MyApplication.getId(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getInfo().equals("success")) {
                                    isFavorite(question);
                                } else {
                                    ToastUtil.makeToast(response.getInfo());
                                }

                            }

                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("???????????????,?????????????????????");
                            }
                        });

            }
        });
    }

    private void loadMore(String url, String param, TailViewHolder holder){
        if (questionType == TYPE_FAVORITE)
//            Log.d("ISLODEMORE?", "????????????????????????");
        isLoading = true;
        //????????????10???  ???????????????????????????10????????????  ????????????
        if (mQuestionList.size() % 10 != 0) {
            holder.loadingTextView.setText("???????????????");
            isLoading = false;
            return;
        }


        holder.loadingTextView.setText("?????????...");
        if (questionType == TYPE_FAVORITE && isLoading){
            holder.loadingTextView.setText("?????????????????????");
            isLoading = false;
            return;
        }


        //???????????????
        HttpUtil.sendHttpRequest(url, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {
                if (response.getInfo().equals("success")) {
//                    if (mQuestionList.size() == Integer.parseInt(JsonParse.getElement
//                            (response.getData(), "totalCount"))) {
//                        ToastUtil.makeToast("???????????????###");
//                        isLoading = false;
//                        holder.loadingTextView.setText("???????????????  :)");
//                    }
                    if (JsonParse.getElement(response.getData(), "questions").equals("[]")) {
                        ToastUtil.makeToast("??????????????????");
                        isLoading = false;
                        holder.loadingTextView.setText("??????????????? :)");
                    } else {
                        mQuestionList.addAll(JsonParse.getQuestionList((String)response.getData()));

                        notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeToast(response.getInfo());
                }
            }

            @Override
            public void onFail(String reason) {
                holder.loadingTextView.setText("????????????");
                ToastUtil.makeToast("????????????,???????????????");
                isLoading = false;
            }
        });
    }
}