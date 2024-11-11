package com.example.witchersshoes.Model;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModel implements Serializable {
    private String ID;
    private String title;
    private String description;
    private ArrayList<String> picUrl;
    private int numberInCart;

    private int price;
    private double rating;

    private DocumentReference categoryID;

    public ProductModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public DocumentReference getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(DocumentReference categoryID) {
        this.categoryID = categoryID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
}
