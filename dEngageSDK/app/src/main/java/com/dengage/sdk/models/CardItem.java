package com.dengage.sdk.models;

import com.google.gson.annotations.SerializedName;

public class CardItem {

    @SerializedName("productId")
    private String productId;

    @SerializedName("variantId")
    private String variantId;

    @SerializedName("price")
    private double price;

    @SerializedName("discountedPrice")
    private double discountedPrice;

    @SerializedName("currency")
    private String currency;

    @SerializedName("quantity")
    private int quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
