package com.shenzhe.answerquestion.bean;

import java.util.ArrayList;

/**
 * 回答问题
 */

public class Answer {

    private Integer id;
    private Integer uid;
    private Integer qid;
    private String content;
    private String images;
    private Integer exciting;
    private Integer naive;
    private Integer best;
    //回答的日期
    private String date;
    private String authorName;
    private String authorAvatarUrlString;
    private boolean isNaive;
    private boolean isExciting;

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

    public Integer getQid() {
        return qid;
    }

    public void setQid(Integer qid) {
        this.qid = qid;
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

    public Integer getBest() {
        return best;
    }

    public void setBest(Integer best) {
        this.best = best;
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

    public Answer() {
    }

    public Answer(Integer id, Integer uid, Integer qid, String content, String images, Integer exciting, Integer naive, Integer best, String date, String authorName, String authorAvatarUrlString) {
        this.id = id;
        this.uid = uid;
        this.qid = qid;
        this.content = content;
        this.images = images;
        this.exciting = exciting;
        this.naive = naive;
        this.best = best;
        this.date = date;
        this.authorName = authorName;
        this.authorAvatarUrlString = authorAvatarUrlString;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", uid=" + uid +
                ", qid=" + qid +
                ", content='" + content + '\'' +
                ", images='" + images + '\'' +
                ", exciting=" + exciting +
                ", naive=" + naive +
                ", best=" + best +
                ", date='" + date + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorAvatarUrlString='" + authorAvatarUrlString + '\'' +
                '}';
    }
}

