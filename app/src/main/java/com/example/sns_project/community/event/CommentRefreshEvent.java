package com.example.sns_project.community.event;

public class CommentRefreshEvent {
    private String community;

    public CommentRefreshEvent(String community) {
        this.community = community;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
