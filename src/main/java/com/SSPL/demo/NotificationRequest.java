package com.SSPL.demo;

public class NotificationRequest {

    private String title;
    private String body;

    // Default constructor
    public NotificationRequest() {
    }

    // Constructor with parameters
    public NotificationRequest(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
