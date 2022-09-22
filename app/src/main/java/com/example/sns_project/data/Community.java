package com.example.sns_project.data;

import com.example.sns_project.util.DateUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Community implements Serializable {
    private String addedbyUser;
    private String userName;
    private String uid;
    private String title;
    private String content;
    private String imgPath = null;
    private String likeCount = "0";
    private String commentCount = "0";
    private Long timeStemp;
    private Map<String, Boolean> likeUsers = new HashMap<>();

    public Community() {
    }

    public String getAddedbyUser() {
        return addedbyUser;
    }

    public void setAddedbyUser(String addedbyUser) {
        this.addedbyUser = addedbyUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setLikeCount(String likeCount) {
        if (Integer.parseInt(likeCount) < 0) {
            likeCount = "0";
        }
        this.likeCount = likeCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public Long getTimeStemp() {
        return timeStemp;
    }

    public String getTimeFormat() {
        return DateUtils.getConvertTime(timeStemp);
    }

    public void setTimeStemp(Long timeStemp) {
        this.timeStemp = timeStemp;
    }

    public Map<String, Boolean> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(Map<String, Boolean> likeUsers) {
        this.likeUsers = likeUsers;
    }

    @Override
    public String toString() {
        return "Community{" +
                "addedbyUser='" + addedbyUser + '\'' +
                ", userName='" + userName + '\'' +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                '}';
    }
}
