package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class EssentialList implements Serializable {

    private String name, id, image_url;
    private int qty, category, discount;
    private float cost;
    private boolean isAdd;

    public EssentialList() {
        this.name = "";
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
        this.isAdd = false;
        this.discount = 0;
    }

    public EssentialList(String name) {
        this.name = name;
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
        this.isAdd = false;
        this.discount = 0;
    }

    public int getDiscount() {
        return discount;
    }

    public EssentialList setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public int getCategory() {
        return category;
    }

    public EssentialList setCategory(int category) {
        this.category = category;
        return this;
    }

    public String getImage_url() {
        return image_url;
    }

    public EssentialList setImage_url(String image_url) {
        this.image_url = image_url;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public EssentialList setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public EssentialList setCost(float cost) {
        this.cost = cost;
        return this;
    }

    public int getQty() {
        return qty;
    }

    public EssentialList setQty(int qty) {
        this.qty = qty;
        return this;
    }
}
