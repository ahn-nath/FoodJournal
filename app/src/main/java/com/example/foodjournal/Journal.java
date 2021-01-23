package com.example.foodjournal;

public class Journal {
    private String title;
    private String description;
    private int priority;


    public Journal() {
        //empty constructor needed
    }
    public Journal(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
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
