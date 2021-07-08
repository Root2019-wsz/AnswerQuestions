package com.shenzhe.answerquestion.util;

import android.util.Log;

import com.shenzhe.answerquestion.bean.Answer;
import com.shenzhe.answerquestion.bean.Person;
import com.shenzhe.answerquestion.bean.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON解析
 */

public class JsonParse {
    private static final String TAG = "JSON";
    public static Person getUser(String data){
        Person person = new Person();
        try{
                JSONObject userData = new JSONObject(data);
                person.setId(userData.getInt("id"));
                person.setUsername(userData.getString("username"));
                person.setPassword(userData.getString("password"));
                if(userData.has("avatar")){
                    person.setAvatar(userData.getString("avatar"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return person;
    }

    public static List<Question> getQuestionList(String data){
        List<Question> questionList = new ArrayList<>();
        try {
            JSONObject dataAll = new JSONObject(data);
//            JSONObject questionObject = dataAll.getJSONObject("data");
            JSONArray questionArrary = dataAll.getJSONArray("questions");
            for (int i = 0; i < questionArrary.length(); i++) {
                JSONObject one = questionArrary.getJSONObject(i);
                Question question = new Question();
                question.setId(one.getInt("id"));
                question.setAuthorName(one.getString("authorName"));
                question.setAuthorAvatarUrlString(one.getString("authorAvatarUrlString"));
                question.setTitle(one.getString("title"));
                question.setContent(one.getString("content"));
                question.setDate(one.getString("date"));
                question.setRecent(one.getString("recent"));
                if(!MyTextUtils.isNull(one.getString("images"))){
                    question.setImages(one.getString("images"));
                }else {
                    question.setImages(null);
                }
                question.setExciting(one.getInt("exciting"));
                question.setNaive(one.getInt("naive"));
                question.setAnswerCount(one.getInt("answerCount"));
                question.setUid(one.getInt("uid"));
                question.setId(one.getInt("id"));
                question.setImages(one.getString("authorAvatarUrlString"));
//                question.setExciting(one.getBoolean("is_exciting"));
//                question.setNaive(one.getBoolean("is_naive"));
//                if (one.has("is_favorite"))
//                    question.setFavorite(one.getBoolean("is_favorite"));
//                else question.setFavorite(true);
                Log.d(TAG, "getQuestionList: " + question.toString());
                questionList.add(question);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return questionList;
    }

    public static List<Answer> getAnswerList(String content) {
        List<Answer> answerList = new ArrayList<>();
        try {

            JSONObject data = new JSONObject(content);
            JSONArray answerArray = data.getJSONArray("answer");
            for (int i = 0; i < answerArray.length(); i++) {
                Answer answer = new Answer();
                JSONObject ansOne = answerArray.getJSONObject(i);
                answer.setId(ansOne.getInt("id"));
                answer.setContent(ansOne.getString("content"));

                if (!MyTextUtils.isNull(ansOne.getString("images"))) {
                    answer.setImages(ansOne.getString("images"));
                } else answer.setImages(null);
                answer.setDate(ansOne.getString("date"));
                answer.setBest(ansOne.getInt("best"));
                answer.setExciting(ansOne.getInt("exciting"));
                answer.setNaive(ansOne.getInt("naive"));
                answer.setUid(ansOne.getInt("uid"));
                answer.setId(ansOne.getInt("id"));
                answer.setAuthorName(ansOne.getString("authorName"));
                answer.setAuthorAvatarUrlString(ansOne.getString("authorAvatarUrlString"));
//                answer.setExciting(ansOne.getBoolean("is_exciting"));
//                answer.setNaive(ansOne.getBoolean("is_naive"));
                answerList.add(answer);
                Log.d(TAG, "answer : " + answer.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.makeToast("回答解析异常");
        }
        return answerList;
    }

    public static String getElement(String data, String name) {
        try {
            if (data!=null){
                    JSONObject js = new JSONObject(data);
                    if (js.has(name)) {
                        return js.getString(name);
                }
                //检查存在才返回
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("tag", e.toString());
        }
        return null;
    }
}
