package com.social.model;

public class User_selcetion {
    boolean is_selected;
    String name;
    String lang;

    public User_selcetion(boolean is_selected, String name,String lang) {
        this.is_selected = is_selected;
        this.name = name;
        this.lang = lang;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }
}
