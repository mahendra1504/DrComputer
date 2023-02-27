package com.example.drcomputer;

public class MyOrderItemModel {
    private String productImage;
    private String productTitle;
    private float rating;
    private String productID;

    public MyOrderItemModel(String productImage, String productTitle, float rating, String productID) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.rating = rating;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
