package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class EssentialList implements Serializable {

    private String name, id, image_url;
    private int qty, category, discount, minimum_qty;
    private float cost, cost_100, cost_200, cost_500, cost_1000, cost_10000, mrp;
    private boolean isAdd;

    public EssentialList() {
        this.name = "";
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
        this.isAdd = false;
        this.discount = 0;
        this.cost_100 = 0;
        this.cost_200 = 0;
        this.cost_500 = 0;
        this.cost_1000 = 0;
        this.cost_10000 = 0;
        this.mrp = 135;
        this.minimum_qty = 0;
    }

    public EssentialList(String name) {
        this.name = name;
        this.qty = 0;
        this.cost = 135;
        this.image_url = "";
        this.isAdd = false;
        this.discount = 0;
        this.cost_100 = 0;
        this.cost_200 = 0;
        this.cost_500 = 0;
        this.cost_1000 = 0;
        this.cost_10000 = 0;
        this.mrp = 135;
        this.minimum_qty = 0;
    }

    public float getCost_100() {
        return cost_100;
    }

    public EssentialList setCost_100(float cost_100) {
        this.cost_100 = cost_100;
        return this;
    }

    public float getCost_200() {
        return cost_200;
    }

    public EssentialList setCost_200(float cost_200) {
        this.cost_200 = cost_200;
        return this;
    }

    public float getCost_500() {
        return cost_500;
    }

    public EssentialList setCost_500(float cost_500) {
        this.cost_500 = cost_500;
        return this;
    }

    public float getCost_1000() {
        return cost_1000;
    }

    public EssentialList setCost_1000(float cost_1000) {
        this.cost_1000 = cost_1000;
        return this;
    }

    public float getCost_10000() {
        return cost_10000;
    }

    public EssentialList setCost_10000(float cost_10000) {
        this.cost_10000 = cost_10000;
        return this;
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

    public float getTotalCost() {
        if (this.qty >= 10000) {
            return this.qty * this.cost_10000;
        } else if (this.qty >= 1000) {
            return this.qty * this.cost_1000;
        } else if (this.qty >= 500){
            return this.qty * this.cost_500;
        } else if (this.qty >= 200){
            return this.qty * this.cost_200;
        } else if (this.qty >= 100){
            return this.qty * this.cost_100;
        }
        return this.qty * this.cost;
    }

    public double getTotalCostWithDiscount() {
        if (this.qty >= 10000) {
            return this.qty * this.cost_10000 * this.discount * 0.01;
        } else if (this.qty >= 1000) {
            return this.qty * this.cost_1000 * this.discount * 0.01;
        } else if (this.qty >= 500){
            return this.qty * this.cost_500 * this.discount * 0.01;
        } else if (this.qty >= 200){
            return this.qty * this.cost_200 * this.discount * 0.01;
        } else if (this.qty >= 100){
            return this.qty * this.cost_100 * this.discount * 0.01;
        }
        return this.qty * this.cost * this.discount * 0.01;
    }

    public float getMrp() {
        return mrp;
    }

    public EssentialList setMrp(float mrp) {
        this.mrp = mrp;
        return this;
    }

    public int getMinimum_qty() {
        return minimum_qty;
    }

    public EssentialList setMinimum_qty(int minimum_qty) {
        this.minimum_qty = minimum_qty;
        return this;
    }
}
