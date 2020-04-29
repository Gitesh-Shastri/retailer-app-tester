package com.example.salestargetapp.data;

public class PharmacyWithSales {

    String name, sales;

    public PharmacyWithSales(String name, String sales) {
        this.name = name;
        this.sales = sales;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
}
