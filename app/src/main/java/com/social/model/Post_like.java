package com.social.model;

import java.util.ArrayList;

public class Post_like {
    String postid;
    ArrayList<Like> likes;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }
}
