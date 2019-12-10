package com.medicento.retailerappmedi.data.order_related;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private String name, company, reason, id, item_code;
    private int qty, price;
    private boolean isSelected, isReturned, isAlreadyReturned;

    public OrderItem(String name, String company, int qty, int price) {
        this.name = name;
        this.company = company;
        this.qty = qty;
        this.price = price;
        this.isSelected = false;
        this.isAlreadyReturned  = false;
        this.isReturned = false;
        this.reason = "";
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public boolean isAlreadyReturned() {
        return isAlreadyReturned;
    }

    public void setAlreadyReturned(boolean alreadyReturned) {
        isAlreadyReturned = alreadyReturned;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    public OrderItem() {
    }

    public OrderItem setName(String name) {
        this.name = name;
        return this;
    }

    public OrderItem setCompany(String company) {
        this.company = company;
        return this;
    }

    public OrderItem setQty(int qty) {
        this.qty = qty;
        return this;
    }

    public OrderItem setPrice(int price) {
        this.price = price;
        return this;
    }

    public OrderItem createOrderItem() {
        return new OrderItem(name, company, qty, price);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                ", isSelected=" + isSelected +
                '}';
    }
}
