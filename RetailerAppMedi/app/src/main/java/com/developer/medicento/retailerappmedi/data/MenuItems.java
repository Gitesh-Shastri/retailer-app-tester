package com.developer.medicento.retailerappmedi.data;

import android.graphics.drawable.Drawable;

public class MenuItems {

    String name, id;
    Drawable drawable;

    public MenuItems(String name, String id, Drawable drawable) {
        this.name = name;
        this.id = id;
        this.drawable = drawable;
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

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
