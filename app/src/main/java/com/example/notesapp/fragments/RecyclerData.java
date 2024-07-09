package com.example.notesapp.fragments;

public class RecyclerData {
    // string for displaying
    // title and description.
    private String title;

    // constructor for our title and description.
    public RecyclerData(String title) {
        this.title = title;
    }
    public RecyclerData(){}

    // creating getter and setter methods.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
