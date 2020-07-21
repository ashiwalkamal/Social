package com.social.model;

import java.util.ArrayList;

public class Post_comments {
    String postid;
    ArrayList<Comment> comments;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
