package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private String name, company, reason, id, item_code, packing, scheme, dispute;
    private int qty, price, bounce_qty;
    private boolean isSelected, isReturned, isAlreadyReturned, is_executed, isDispute;

    public OrderItem(String name, String company, int qty, int price) {
        this.name = name;
        this.company = company;
        this.qty = qty;
        this.price = price;
        this.isSelected = false;
        this.isAlreadyReturned  = false;
        this.isReturned = false;
        this.is_executed = false;
        this.reason = "";
    }

    public String getDispute() {
        return dispute;
    }

    public void setDispute(String dispute) {
        this.dispute = dispute;
    }

    public boolean isDispute() {
        return isDispute;
    }

    public void setDispute(boolean dispute) {
        isDispute = dispute;
    }

    public int getBounce_qty() {
        return bounce_qty;
    }

    public void setBounce_qty(int bounce_qty) {
        this.bounce_qty = bounce_qty;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean isIs_executed() {
        return is_executed;
    }

    public void setIs_executed(boolean is_executed) {
        this.is_executed = is_executed;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
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
