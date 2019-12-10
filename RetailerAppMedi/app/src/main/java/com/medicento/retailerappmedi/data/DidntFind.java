package com.medicento.retailerappmedi.data;

import java.io.Serializable;

public class DidntFind implements Serializable {

    String name;
    int quantity;

    public DidntFind() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
