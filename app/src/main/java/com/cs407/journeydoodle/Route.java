package com.cs407.journeydoodle;

public class Route {
    private int id;
    private String date;
    private String title;
    private String content;
    private String username;


    public Route(int id, String date, String username, String title, String content) {
        this.id = id;
        this.date = date;
        this.username = username;
        this.title = title;
        this.content = content;
    }

    public int getId() {return id; }

    public String getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
