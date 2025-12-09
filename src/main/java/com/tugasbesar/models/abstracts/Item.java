package com.tugasbesar.models.abstracts;

public abstract class Item {
    protected String name;

    protected boolean isEdible;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEdible(boolean isEdible) {
        this.isEdible = isEdible;
    }
    
    public boolean isEdible() {
        return isEdible;
    }

    @Override
    public String toString() {
        return name;
    }
}