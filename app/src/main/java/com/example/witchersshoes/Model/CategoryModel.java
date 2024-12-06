package com.example.witchersshoes.Model;

public class CategoryModel {
    private String title;
    private String id;
    private String picUrl;

    public CategoryModel() {
    }

    public CategoryModel(String title, String id, String picUrl) {
        this.title = title;
        this.id = id;
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
