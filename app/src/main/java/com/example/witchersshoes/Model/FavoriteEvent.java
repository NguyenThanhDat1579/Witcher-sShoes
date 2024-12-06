package com.example.witchersshoes.Model;

// Tạo class FavoriteEvent
public class FavoriteEvent {
    private String productId;
    private boolean isFavorite;

    public FavoriteEvent(String productId, boolean isFavorite) {
        this.productId = productId;
        this.isFavorite = isFavorite;
    }

    public String getProductId() {
        return productId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
