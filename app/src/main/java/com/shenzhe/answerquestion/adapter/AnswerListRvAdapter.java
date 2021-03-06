package com.shenzhe.answerquestion.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.shenzhe.answerquestion.R;
import com.shenzhe.answerquestion.bean.Answer;
import com.shenzhe.answerquestion.bean.Question;
import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.DateUtil;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.JsonParse;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.MyTextUtils;
import com.shenzhe.answerquestion.util.ToastUtil;
import com.shenzhe.answerquestion.view.RoundImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnswerListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_QUESTION = 1;
    private static final int TYPE_TAIL = 2;

    private List<Answer> mAnswerList;
    public static boolean needToLoad = true;
    private static Question question;
    private String imageUrl;

    public AnswerListRvAdapter(List<Answer> answerList , Question question){
        this.mAnswerList = answerList;
        AnswerListRvAdapter.question = question;
        if (question.getImages() != null){
            imageUrl = question.getImages();
        }
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        ScrollView allImage;

        RoundImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;
        ImageView picture;
        ArrayList<ImageView> imageViews;

        QuestionViewHolder(View itemView){
            super(itemView);
            imageViews = new ArrayList<>();

            title = itemView.findViewById(R.id.tv_question_title);
            authorName = itemView.findViewById(R.id.tv_question_authorname);
            content = itemView.findViewById(R.id.tv_question_content);
            updateTime = itemView.findViewById(R.id.tv_question_update_time);
            answerNum = itemView.findViewById(R.id.tv_question_answer_num);
            excitingNum = itemView.findViewById(R.id.tv_question_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_question_naive_num);
            date = itemView.findViewById(R.id.tv_ask_date);

            avatar = itemView.findViewById(R.id.image_question_avatar);
            comment = itemView.findViewById(R.id.image_question_comment);
            exciting = itemView.findViewById(R.id.image_question_exciting);
            naive = itemView.findViewById(R.id.image_question_naive);
            favorite = itemView.findViewById(R.id.image_question_favorite);

            allImage = itemView.findViewById(R.id.scroll_question_image_all);
            picture = itemView.findViewById(R.id.question_image_1);
            imageViews.add(picture);
        }
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder{

        TextView authorName;
        TextView content;

        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        RoundImageView avatar;
        ImageView exciting;
        ImageView naive;

        ScrollView allImage;
        ImageView picture;
        ArrayList<ImageView> imageViews;
        String imageUrl;

        NormalViewHolder(View itemView){
            super(itemView);
            imageViews = new ArrayList<>();
            authorName = itemView.findViewById(R.id.tv_answer_author_name);
            content = itemView.findViewById(R.id.tv_answer_content);
            excitingNum = itemView.findViewById(R.id.tv_answer_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_answer_naive_num);
            date = itemView.findViewById(R.id.tv_answer_date);

            avatar = itemView.findViewById(R.id.image_answer_avatar);
            exciting = itemView.findViewById(R.id.image_answer_exciting);
            naive = itemView.findViewById(R.id.image_answer_naive);

            allImage = itemView.findViewById(R.id.scroll_answer_image_all);
            picture = itemView.findViewById(R.id.answer_image_1);
            imageViews.add(picture);
        }
    }

    static class TailViewHolder extends RecyclerView.ViewHolder{
        private TextView loadingTextView;
        TailViewHolder(View itemview){
            super(itemview);
            loadingTextView = itemview.findViewById(R.id.tv_loading_tail);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case TYPE_NORMAL:
                NormalViewHolder holder = new NormalViewHolder(inflater.
                        inflate(R.layout.answer_item,parent,false));
                initItemListener(holder, null);
                return holder;
            case TYPE_TAIL:
                return new TailViewHolder(inflater.
                        inflate(R.layout.tail_item, parent, false));
            case TYPE_QUESTION:
                QuestionViewHolder qHolder = new QuestionViewHolder(inflater.
                        inflate(R.layout.question_item, parent, false));
                initItemListener(null, qHolder);
                return qHolder;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_QUESTION;
        }
        return position == mAnswerList.size() + 1 ? TYPE_TAIL : TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mAnswerList.size()+2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_NORMAL:
                if (mAnswerList.size()>0) {
                    Answer answer = mAnswerList.get(position - 1);
                    NormalViewHolder normalHolder = (NormalViewHolder) holder;
                    normalHolder.authorName.setText(answer.getAuthorName());
                    normalHolder.date.setText("?????????" + answer.getDate());
                    normalHolder.content.setText(answer.getContent());
                    normalHolder.excitingNum.setText(String.valueOf(answer.getExciting()));
                    normalHolder.naiveNum.setText(String.valueOf(answer.getNaive()));

                    if (!MyTextUtils.isNull(question.getAuthorAvatarUrlString())) {
                        HttpUtil.loadImage(answer.getAuthorAvatarUrlString(),
                                (Bitmap bitmap, String info) -> {
                                    if ("success".equals(info))
                                        normalHolder.avatar.setImageBitmap(bitmap);
                                    else normalHolder.avatar.setImageResource(R.drawable.nav_icon);
                                });
                    }

                    normalHolder.imageUrl = answer.getImages();

                    if (normalHolder.imageUrl != null) {
                        HttpUtil.loadImage(normalHolder.imageUrl, (bitmap, info) -> {
                            if ("success".equals(info)) {
                                normalHolder.imageViews.get(0).setVisibility(View.VISIBLE);
                                normalHolder.imageViews.get(0).setImageBitmap(bitmap);
                            } else
                                normalHolder.imageViews.get(0).setVisibility(View.GONE);
                        });
                    }

                    if (answer.isExciting()) {
                        normalHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                    } else {
                        normalHolder.exciting.setImageResource(R.drawable.ic_exciting);
                    }

                    if (answer.isNaive()) {
                        normalHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                    } else {
                        normalHolder.naive.setImageResource(R.drawable.ic_naive);
                    }
                }
                break;
            case TYPE_TAIL:
                TailViewHolder tailViewHolder = (TailViewHolder)holder;
                if (mAnswerList.size() != 0){
                    if (needToLoad){
                        String param = "qid=" + question.getId();
                        loadMore(ApiParam.GET_ANSWER_LIST, param, tailViewHolder);
                    }
                }else {
                    ((TailViewHolder) holder).loadingTextView.setText("????????????????????????");
                }
                break;
            case TYPE_QUESTION:
                QuestionViewHolder qHolder = (QuestionViewHolder)holder;
                if (!MyTextUtils.isNull(question.getAuthorAvatarUrlString())){
                    HttpUtil.loadImage(question.getAuthorAvatarUrlString(),((bitmap, info) -> {
                        if ("success".equals(info))
                            qHolder.avatar.setImageBitmap(bitmap);
                        else
                            qHolder.avatar.setImageResource(R.drawable.nav_icon);
                    }));
                }
                if (imageUrl != null){
                    qHolder.allImage.setVisibility(View.VISIBLE);
                    HttpUtil.loadImage(imageUrl, (bitmap, info) -> {
                        if ("success".equals(info)){
                            qHolder.imageViews.get(0).setVisibility(View.VISIBLE);
                            qHolder.imageViews.get(0).setImageBitmap(bitmap);
                        }else
                            qHolder.imageViews.get(0).setVisibility(View.GONE);
                    });
                }

                qHolder.authorName.setText(question.getAuthorName());
                qHolder.date.setText("?????????"+question.getDate());
                qHolder.title.setText(question.getTitle());
                qHolder.content.setText(question.getContent());
                if(question.getRecent() != null){
                    qHolder.updateTime.setText(question.getRecent() + "??????");
                }else {
                    qHolder.updateTime.setText("?????????");
                }
                qHolder.answerNum.setText(String.valueOf(question.getAnswerCount()));
                qHolder.excitingNum.setText(String.valueOf(question.getExciting()));
                qHolder.naiveNum.setText(String.valueOf(question.getNaive()));

//                if (!question.isExciting()) {
//                    qHolder.exciting.setImageResource(R.drawable.ic_exciting);
//                } else {
//                    qHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
//                }
//                if (!question.isNaive()) {
//                    qHolder.naive.setImageResource(R.drawable.ic_naive);
//                } else {
//                    qHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
//                }
//                if (!question.isFavorite()) {
//                    qHolder.favorite.setImageResource(R.drawable.ic_favorite);
//                } else {
//                    qHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
//                }

                break;
        }
    }


    private void loadMore(String url, String param, TailViewHolder holder){
        Log.d("LOAD_dd", "LoadMore ??????");
        if (mAnswerList.size() % 10 != 0) {
            holder.loadingTextView.setText("???????????????");
            needToLoad = false;
            return;
        }
        holder.loadingTextView.setText("?????????...");
        HttpUtil.sendHttpRequest(url, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {
                if (response.getInfo().equals("success")){
                    Log.d("getAnswerListSize", "size=: " + JsonParse.getAnswerList((String)response.getData()).size());
                    Log.d("getAnswerListSize", "size=: " + JsonParse.getAnswerList((String)response.getData()).size());
//                    if (mAnswerList.size() == Integer.parseInt(JsonParse.getElement
//                            (response.getData(), "totalCount"))) {
//                        needToLoad = false;
//                        holder.loadingTextView.setText("???????????????");
//                    } else
                    if (JsonParse.getAnswerList((String)response.getData()).size() == 0) {
                        ToastUtil.makeToast("??????????????????");
                        needToLoad = false;
                        holder.loadingTextView.setText("???????????????");
                    } else {
                        mAnswerList.addAll(JsonParse.getAnswerList((String)response.getData()));
                        notifyDataSetChanged();
                    }
                }else {
                    ToastUtil.makeToast(response.getInfo());
                }
            }

            @Override
            public void onFail(String reason) {
                holder.loadingTextView.setText("????????????");
                ToastUtil.makeToast("????????????,???????????????");
                needToLoad = false;
            }
        });
    }

//    public boolean isNaive(Answer answer) {
//        HttpUtil.sendHttpRequest(ApiParam.IS_NATIVE_ANSWER, "aid=" + answer.getId() + "&uid="+ MyApplication.getId(),
//                new HttpUtil.HttpCallBack() {
//                    @Override
//                    public void onResponse(HttpUtil.Response response) {
//                        if (response.getInfo().equals("success")) {
//                            answer.setNaive(answer.getNaive());
//                            answer.setNaive(true);
//                        } else {
//                            ToastUtil.makeToast(response.getInfo());
//                        }
//                    }
//                    @Override
//                    public void onFail(String reason) {
//                        ToastUtil.makeToast("????????????????????????????????????");
//                    }
//                });
//        return isNaive(answer);
//    }
//
//    public boolean isExciting(Answer answer) {
//        HttpUtil.sendHttpRequest(ApiParam.IS_EXCITING_ANSWER, "aid=" + answer.getId() + "&uid="+ MyApplication.getId(),
//                new HttpUtil.HttpCallBack() {
//                    @Override
//                    public void onResponse(HttpUtil.Response response) {
//                        if (response.getInfo().equals("success")) {
//                            answer.setExciting(answer.getNaive());
//                            answer.setExciting(true);
//                        } else {
//                            ToastUtil.makeToast(response.getInfo());
//                        }
//                    }
//                    @Override
//                    public void onFail(String reason) {
//                        ToastUtil.makeToast("????????????????????????????????????");
//                    }
//                });
//        return isExciting(answer);
//    }


    private void initItemListener(NormalViewHolder holder, QuestionViewHolder qHolder){
        if (holder != null){
            holder.exciting.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Answer answer = mAnswerList.get(position - 1);
                HttpUtil.sendHttpRequest(ApiParam.IS_EXCITING_ANSWER, "aid=" + answer.getId() + "&uid="+ MyApplication.getId(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getData().equals("false") && response.getInfo().equals("success")){
                                    holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                                    answer.setExciting(answer.getExciting());
                                    answer.setExciting(true);
                                    addExciting(holder,null,answer);
                                }else {
                                    holder.exciting.setImageResource(R.drawable.ic_exciting);
                                    answer.setExciting(answer.getExciting());
                                    answer.setExciting(false);
                                    cancelExciting(holder,null,answer);
                                }
//                                if (response.getInfo().equals("success")) {
//                                    answer.setExciting(answer.getNaive());
//                                    answer.setExciting(true);
//                                } else {
//                                    ToastUtil.makeToast(response.getInfo());
//                                }
                            }
                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("????????????????????????????????????");
                            }
                        });
//                if (!isNaive(answer)) {
//                    if (!isExciting(answer)) {
//                        //???????????? ?????? ??????????????????+1
//                        addExciting(holder, null, answer);//????????????
//                    } else {
//                        //????????????  ????????? ??????????????????-1
//                        cancelExciting(holder, null, answer);//????????????
//                    }
//                } else {
//                    //????????????,?????????
//                    cancelNaive(holder, null, answer);
//                    addExciting(holder, null, answer);
//                }
            });
            holder.naive.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Answer answer = mAnswerList.get(position - 1);
                HttpUtil.sendHttpRequest(ApiParam.IS_NATIVE_ANSWER, "aid=" + answer.getId() + "&uid="+ MyApplication.getId(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getData().equals("false") && response.getInfo().equals("success")){
                                    holder.naive.setImageResource(R.drawable.ic_naive_clicked);
                                    answer.setNaive(answer.getNaive());
                                    answer.setNaive(true);
                                    addNaive(holder,null,answer);
                                }else {
                                    holder.naive.setImageResource(R.drawable.ic_naive);
                                    answer.setNaive(answer.getNaive());
                                    answer.setNaive(false);
                                    cancelNaive(holder,null,answer);
                                }
//                                if (response.getInfo().equals("success")) {
//                                    answer.setNaive(answer.getNaive());
//                                    answer.setNaive(true);
//                                } else {
//                                    ToastUtil.makeToast(response.getInfo());
//                                }
                            }
                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("????????????????????????????????????");
                            }
                        });
//                if (!isExciting(answer)) {
//                    if (!isNaive(answer)) {
//                        //????????????  ??? ??????????????????-1
//                        holder.naive.setImageResource(R.drawable.ic_naive);
//                        addNaive(holder, null, answer);
//                    } else {
//                        //????????????  ??? ??????????????????-1
//                        holder.naive.setImageResource(R.drawable.ic_naive_clicked);
//                        cancelNaive(holder, null, answer);
//                    }
//                } else {
//                    //????????????,?????????
//                    cancelExciting(holder, null, answer);
//                    addNaive(holder, null, answer);
//                }
            });
        }else if (qHolder != null){
            qHolder.exciting.setOnClickListener(v -> {
                if (!question.isNaive()) {
                    if (!question.isExciting()) {
                        qHolder.exciting.setImageResource(R.drawable.ic_exciting);
                        // ???????????? ?????? ??????????????????+1
                        addExciting(null, qHolder, null);//????????????
                    } else {
                        qHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                        //????????????  ????????? ??????????????????-1
                        cancelExciting(null, qHolder, null);//????????????
                    }
                } else {
                    //????????????,?????????
                    cancelNaive(null, qHolder, null);
                    addExciting(null, qHolder, null);
                }
            });
            qHolder.naive.setOnClickListener(v -> {
                if (!question.isExciting()) {
                    if (!question.isNaive()) {
                        qHolder.naive.setImageResource(R.drawable.ic_naive);
                        //TODO????????????  ??? ??????????????????-1
                        addNaive(null, qHolder, null);
                    } else {
                        qHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                        //TODO????????????  ??? ??????????????????-1
                        cancelNaive(null, qHolder, null);
                    }
                } else {
                    //????????????,?????????
                    cancelExciting(null, qHolder, null);
                    addNaive(null, qHolder, null);
                }
            });
            qHolder.favorite.setOnClickListener(v -> {
                qHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
                if (!question.isFavorite()) {
                    //????????????  ??????
                    HttpUtil.sendHttpRequest(ApiParam.ADD_FAVORITE, "qid=" + question.getId()
                                    + "&uid=" + MyApplication.getId(),
                            new HttpUtil.HttpCallBack() {
                                @Override
                                public void onResponse(HttpUtil.Response response) {
                                    if (response.getInfo().equals("success")) {
                                        question.setFavorite(true);
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
                    qHolder.favorite.setImageResource(R.drawable.ic_favorite);
                    HttpUtil.sendHttpRequest(ApiParam.CANCEL_FAVORITE, "qid=" + question.getId()
                                    + "&uid=" + MyApplication.getId(),
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
                                    ToastUtil.makeToast("???????????????,?????????????????????");
                                }
                            });
                }


            });
        }
    }

    private void addExciting(@Nullable NormalViewHolder holder, @Nullable QuestionViewHolder qHolder, @Nullable Answer answer){
        String param = null;
        if (holder != null && answer != null) {
            holder.excitingNum.setText(String.valueOf(answer.getExciting()));
            holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
            param = "aid=" + answer.getId()+"&uid="+MyApplication.getId();
        }else if (qHolder != null) {
            qHolder.excitingNum.setText(String.valueOf(question.getExciting()));
            qHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
            param = "qid=" + question.getId()+"&uid="+MyApplication.getId();
        }

        HttpUtil.sendHttpRequest(ApiParam.ADD_EXCITING_ANSWER, param,
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setExciting(answer.getExciting());
//                                answer.setExciting(true);
                            } else {
                                question.setExciting(question.getExciting());
//                                question.setExciting(true);
                            }
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????");
                    }
                });
    }

    private void cancelExciting(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {
        String param = null;
        if (holder != null && answer != null) {
            holder.excitingNum.setText(String.valueOf(answer.getExciting()));
            holder.exciting.setImageResource(R.drawable.ic_exciting);
            param = "aid=" + answer.getId()+"&uid="+ MyApplication.getId();
        } else if (qHolder != null) {
            qHolder.excitingNum.setText(String.valueOf(question.getExciting()));
            qHolder.exciting.setImageResource(R.drawable.ic_exciting);
            param = "qid=" + question.getId()+"&uid="+ MyApplication.getId();
        }

        HttpUtil.sendHttpRequest(ApiParam.DELETE_EXCITING_ANSWER, param,
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setExciting(answer.getExciting());
//                                answer.setExciting(false);
                            } else {
                                question.setExciting(question.getExciting());
//                                question.setExciting(false);
                            }
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????");
                    }
                });

    }

    private void addNaive(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {
        String param = null;
        if (holder != null && answer != null) {
            holder.naiveNum.setText(String.valueOf(answer.getNaive()));
            holder.naive.setImageResource(R.drawable.ic_naive_clicked);
            param = "aid=" + answer.getId()+"&uid="+MyApplication.getId();
        } else if (qHolder != null) {
            qHolder.naiveNum.setText(String.valueOf(question.getNaive()));
            qHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
            param = "qid=" + question.getId()+"&uid="+MyApplication.getId();
        }
        HttpUtil.sendHttpRequest(ApiParam.ADD_NATIVE_ANSWER, param,
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setNaive(answer.getNaive());
//                                answer.setNaive(true);
                            } else {
                                question.setNaive(question.getNaive());
//                                question.setNaive(true);
                            }
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????");
                    }
                });
    }

    private void cancelNaive(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {

        String param = null;
        if (holder != null && answer != null) {
            holder.naiveNum.setText(String.valueOf(answer.getNaive()));
            holder.naive.setImageResource(R.drawable.ic_naive);
            param = "aid=" + answer.getId()+"&uid="+MyApplication.getId();
        } else if (qHolder != null) {
            qHolder.naiveNum.setText(String.valueOf(question.getNaive()));
            qHolder.naive.setImageResource(R.drawable.ic_naive);
            param = "qid=" + question.getId()+"&uid="+MyApplication.getId();
        }

        HttpUtil.sendHttpRequest(ApiParam.DELETE_NATIVE_ANSWER, param,
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setNaive(answer.getNaive());
                                answer.setNaive(false);
                            } else {
                                question.setNaive(question.getNaive());
                                question.setNaive(false);
                            }
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("????????????????????????????????????");
                    }
                });
    }
}