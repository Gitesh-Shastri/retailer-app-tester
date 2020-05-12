package com.medicento.retailerappmedi.data;

public class Category {

    public String name, image_url, id;

    public Category(String name) {
        this.id = "";
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category setImage_url(String image_url) {
        this.image_url = image_url;
        return this;
    }

    public String getId() {
        return id;
    }

    public Category setId(String id) {
        this.id = id;
        return this;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }
}
