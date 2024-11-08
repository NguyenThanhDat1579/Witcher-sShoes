package com.example.witchersshoes.classes;


public class Category1 {
    private String name;
    private int price;
    private int imageResId;

    public Category1(String name, int price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public int getImageResId() {
        return imageResId;
    }
}

