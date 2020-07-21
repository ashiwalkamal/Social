package com.social.model;

public class Interset_selcetion {
    boolean is_selected;
    String name,icon;

    public Interset_selcetion(boolean is_selected, String name,String icon) {
        this.is_selected = is_selected;
        this.name = name;
        this.icon=icon;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
