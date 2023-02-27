package com.example.drcomputer;

public class ProductViewListModel {
    private String productImageLink;
    private String productName;
    private String productPrice;
    private String productRating;
    private String productDetail1;
    private String productDetail2;
    private String productDetail3;
    private String productID;

    public ProductViewListModel(String productImageLink, String productName, String productPrice, String productRating, String productDetail1, String productDetail2, String productDetail3, String productID) {
        this.productImageLink = productImageLink;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productRating = productRating;
        this.productDetail1 = productDetail1;
        this.productDetail2 = productDetail2;
        this.productDetail3 = productDetail3;
        this.productID = productID;
    }

    public String getProductImageLink() {
        return productImageLink;
    }

    public void setProductImageLink(String productImageLink) {
        this.productImageLink = productImageLink;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductRating() {
        return productRating;
    }

    public void setProductRating(String productRating) {
        this.productRating = productRating;
    }

    public String getProductDetail1() {
        return productDetail1;
    }

    public void setProductDetail1(String productDetail1) {
        this.productDetail1 = productDetail1;
    }

    public String getProductDetail2() {
        return productDetail2;
    }

    public void setProductDetail2(String productDetail2) {
        this.productDetail2 = productDetail2;
    }

    public String getProductDetail3() {
        return productDetail3;
    }

    public void setProductDetail3(String productDetail3) {
        this.productDetail3 = productDetail3;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
