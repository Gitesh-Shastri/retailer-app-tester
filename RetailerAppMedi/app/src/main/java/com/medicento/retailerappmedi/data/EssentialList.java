package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class EssentialList implements Serializable {

    private String name, id, image_url;
    private int qty;
    private float cost;

    public EssentialList() {
        this.name = "";
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
    }

    public EssentialList(String name) {
        this.name = name;
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
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
