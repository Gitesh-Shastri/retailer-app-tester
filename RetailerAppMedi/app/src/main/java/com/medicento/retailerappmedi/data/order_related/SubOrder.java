package com.medicento.retailerappmedi.data.order_related;

import java.io.Serializable;
import java.util.ArrayList;

public class SubOrder implements Serializable {

    private String status;
    private String id, suplier_name;
    private int total;
    private ArrayList<OrderItem> orderItems;

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public SubOrder setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
        return this;
    }

    public SubOrder() {
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public int getTotal() {
        return total;
    }

    public SubOrder(String status, String id, int total, String suplier_name) {
        this.status = status;
        this.id = id;
        this.suplier_name = suplier_name;
        this.total = total;
    }

    public SubOrder setStatus(String status) {
        this.status = status;
        return this;
    }

    public SubOrder setId(String id) {
        this.id = id;
        return this;
    }

    public SubOrder setTotal(int total) {
        this.total = total;
        return this;
    }

    public String getSuplier_name() {
        return suplier_name;
    }

    public SubOrder setSuplier_name(String suplier_name) {
        this.suplier_name = suplier_name;
        return this;
    }

    public SubOrder createSubOrder() {
        return new SubOrder(status, id, total, suplier_name);
    }
}
