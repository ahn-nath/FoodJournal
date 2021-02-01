package com.example.foodjournal;

public class Journal {
    private String title;
    private String description;
    private int priority;
    private String image;


    public Journal() {
        //empty constructor needed
    }
    public Journal(String image, String title, String description, int priority) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    public String getImage() {
        return image;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getPriority() {
        return priority;
    }
}
