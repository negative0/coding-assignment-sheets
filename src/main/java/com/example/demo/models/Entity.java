package com.example.demo.models;


public class Entity {
    private String name;
    private int likes, followers;

    public Entity(String name, int likes, int followers) {
        this.name = name;
        this.likes = likes;
        this.followers = followers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
