package com.example.salestargetapp.data;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Area implements Serializable {

    private String name, id;

    public Area(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
