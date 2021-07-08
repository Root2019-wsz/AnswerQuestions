package com.shenzhe.answerquestion.bean;

import com.shenzhe.answerquestion.util.ApiParam;
import com.shenzhe.answerquestion.util.HttpUtil;
import com.shenzhe.answerquestion.util.MyApplication;
import com.shenzhe.answerquestion.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * 提出问题
 */

public class Question implements Serializable {


    private Integer id;
    private Integer uid;
    private Integer answerCount;
    //最近回复的时间
    private String recent;
    private String title;
    //评论文本
    private String content;
    //图片地址
    private String images;
    //数量
    private Integer exciting;
    private Integer naive;
    //问题发布时间
    private String date;
    private String authorName;
    private String authorAvatarUrlString;

    private boolean isNaive = false;
    private boolean isExciting =false;
    private boolean isFavorite = false;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getExciting() {
        return exciting;
    }

    public void setExciting(Integer exciting) {
        this.exciting = exciting;
    }

    public Integer getNaive() {
        return naive;
    }

    public void setNaive(Integer naive) {
        this.naive = naive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatarUrlString() {
        return authorAvatarUrlString;
    }

    public void setAuthorAvatarUrlString(String authorAvatarUrlString) {
        this.authorAvatarUrlString = authorAvatarUrlString;
    }

    public boolean isNaive() {
        return isNaive;
    }

    public void setNaive(boolean naive) {
        isNaive = naive;
    }

    public boolean isExciting() {
        return isExciting;
    }

    public void setExciting(boolean exciting) {
        isExciting = exciting;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Question() {
    }

    public Question(Integer id, Integer uid, Integer answerCount, String recent, String title, String content, String images, Integer exciting, Integer naive, String date, String authorName, String authorAvatarUrlString) {
        this.id = id;
        this.uid = uid;
        this.answerCount = answerCount;
        this.recent = recent;
        this.title = title;
        this.content = content;
        this.images = images;
        this.exciting = exciting;
        this.naive = naive;
        this.date = date;
        this.authorName = authorName;
        this.authorAvatarUrlString = authorAvatarUrlString;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", uid=" + uid +
                ", answerCount=" + answerCount +
                ", recent='" + recent + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", images='" + images + '\'' +
                ", exciting=" + exciting +
                ", naive=" + naive +
                ", date='" + date + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorAvatarUrlString='" + authorAvatarUrlString + '\'' +
                '}';
    }
}

