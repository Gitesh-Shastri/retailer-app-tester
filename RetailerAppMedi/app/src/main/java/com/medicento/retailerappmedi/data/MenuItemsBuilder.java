package com.medicento.retailerappmedi.data;

import android.graphics.drawable.Drawable;

public class MenuItemsBuilder {
    private String name;
    private String id;
    private String description;
    private Drawable drawable;

    public MenuItemsBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MenuItemsBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MenuItemsBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public MenuItemsBuilder setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public MenuItems createMenuItems() {
        return new MenuItems(name, id, drawable, description);
    }
}