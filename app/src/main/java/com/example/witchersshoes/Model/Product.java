package com.example.witchersshoes.Model;

import com.google.firebase.firestore.DocumentReference;

public class Product {
    private String id;
    private String productName;
    private String price;
    private String Image;
    private String description;
    private int Stock;
    private DocumentReference categoryID;

    public Product(){}

    public Product(String id, String productName, String price, String image, String description, int stock, DocumentReference categoryID) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        Image = image;
        this.description = description;
        Stock = stock;
        this.categoryID = categoryID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(DocumentReference categoryID) {
        this.categoryID = categoryID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }
}

