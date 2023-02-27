package com.example.drcomputer;

public class WishlistModel {
    private String productImage;
    private String productTitle;
    private String rating;
    private String productPrice;
    private String productID;

    public WishlistModel(String productImage, String productTitle, String rating, String productPrice, String productID) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.rating = rating;
        this.productPrice = productPrice;
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
