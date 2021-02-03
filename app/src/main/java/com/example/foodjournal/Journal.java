package com.example.foodjournal;

public class Journal {
    private String title;
    private String description;
    private int priority;
    private String image;
    private String date;


    public Journal() {
        //empty constructor needed
    }
    public Journal(String image, String title, String description, int priority, String date) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.date = date;
    }

    public String getDate() {
        return date;
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
