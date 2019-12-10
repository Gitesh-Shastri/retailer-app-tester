package com.medicento.retailerappmedi.data;

public class MedicineAuto {

    private String name, company;
    private float mrp;
    private String scheme, discount, offer_qty, packing;

    public String getOffer_qty() {
        return offer_qty;
    }

    public void setOffer_qty(String offer_qty) {
        this.offer_qty = offer_qty;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public MedicineAuto(String name, String company, float mrp, String scheme, String discount, String offer_qty, String packing) {
        this.name = name;
        this.company = company;
        this.mrp = mrp;
        this.scheme = scheme;
        this.discount = discount;
        this.offer_qty = offer_qty;
        this.packing = packing;
    }

    public MedicineAuto(String name, String company, float mrp, String scheme, String discount, String offer_qty) {
        this.name = name;
        this.company = company;
        this.mrp = mrp;
        this.scheme = scheme;
        this.discount = discount;
        this.offer_qty = offer_qty;
    }

    public MedicineAuto(String name, String company, float mrp, String scheme) {
        this.name = name;
        this.company = company;
        this.mrp = mrp;
        this.scheme = scheme;
    }

    public MedicineAuto(String name, String company, float mrp) {
        this.name = name;
        this.company = company;
        this.mrp = mrp;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }
}
