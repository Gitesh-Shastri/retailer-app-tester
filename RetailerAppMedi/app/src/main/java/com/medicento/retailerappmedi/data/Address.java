package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class Address implements Serializable {

    private String name, id, address, state, city, pincode, area;

    public Address() {
    }

    public String getState() {
        return state;
    }

    public Address setState(String state) {
        this.state = state;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPincode() {
        return pincode;
    }

    public Address setPincode(String pincode) {
        this.pincode = pincode;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public Address setName(String name) {
        this.name = name;
        return this;
    }

    public Address setId(String id) {
        this.id = id;
        return this;
    }

    public Address setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public Address setArea(String area) {
        this.area = area;
        return this;
    }
}
