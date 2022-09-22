package com.example.sns_project.data;

import androidx.annotation.NonNull;

import com.example.sns_project.util.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Message {
    private String addedByuser;
    private String content;
    private Long timeStemp;
    private String communityNo;
    private String userName;

    public Message() {
    }

    public Message(String addedByuser, String content, Long timeStemp, String communityNo, String userName) {
        this.addedByuser = addedByuser;
        this.content = content;
        this.timeStemp = timeStemp;
        this.communityNo = communityNo;
        this.userName = userName;
    }

    public String getAddedByuser() {
        return addedByuser;
    }

    public void setAddedByuser(String addedByuser) {
        this.addedByuser = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getCommunityNo() {
        return communityNo;
    }

    public void setCommunityNo(String communityNo) {
        this.communityNo = communityNo;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "addedByuser='" + addedByuser + '\'' +
                ", content='" + content + '\'' +
                ", timeStemp='" + timeStemp + '\'' +
                ", communityNo='" + communityNo + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
