package com.pait.smartpos.model;

// Created by Android on 1/15/2016.

public class CategoryClass {

    private int Category_ID;
    private String Category, isActive, GSTGroup;

    public int getCategory_ID() {
        return Category_ID;
    }

    public void setCategory_ID(int category_ID) {
        Category_ID = category_ID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getGSTGroup() {
        return GSTGroup;
    }

    public void setGSTGroup(String GSTGroup) {
        this.GSTGroup = GSTGroup;
    }
}
