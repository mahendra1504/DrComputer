package com.example.drcomputer;

public class CartItemModel {
    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    //cart item
    private String productImage;
    private String productTitle;
    private String productPrice;
    private int productQty;
    private String cartItemID;

    public CartItemModel(int type, String productImage, String productTitle, String productPrice, int productQty, String cartItemID) {
        this.type = type;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.cartItemID = cartItemID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    //cart item


    //cart total amount layout

    private String totalAmount;
    private int totalItems;

    public CartItemModel(int type, String totalAmount, int totalItems, String cartItemID) {
        this.type = type;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.cartItemID = cartItemID;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(String cartItemID) {
        this.cartItemID = cartItemID;
    }
}
