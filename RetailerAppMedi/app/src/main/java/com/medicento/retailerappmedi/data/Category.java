package com.medicento.retailerappmedi.data;

public class Category {

    public String name, image_url;

    public Category(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category setImage_url(String image_url) {
        this.image_url = image_url;
        return this;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }
}
