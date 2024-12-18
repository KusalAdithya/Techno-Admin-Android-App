package com.waka.techoadmin.model;

public class Order {

    private String productId,userId;
    private String date,status,orderId;
    private int qty;

    public Order() {
    }

    public Order(String productId, String userId, String date, int qty, String status, String orderId) {
        this.productId = productId;
        this.userId = userId;
        this.date = date;
        this.qty = qty;
        this.status = status;
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
