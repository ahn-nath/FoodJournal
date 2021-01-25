package com.example.foodjournal;

public class Journal {
    private String title;
    private String description;
    private int priority;
    private String ImageUrl;


    public Journal() {
        //empty constructor needed
    }
    public Journal(String mImageUrl, String title, String description, int priority) {
        this.ImageUrl = ImageUrl;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    public String getImageUrl() {
        return ImageUrl;
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
