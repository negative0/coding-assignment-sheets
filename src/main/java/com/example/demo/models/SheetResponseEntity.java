package com.example.demo.models;

import java.util.List;

public class SheetResponseEntity {
    private List<Entity> entities;
    private double totalLikes;
    private double totalFollowers;

    public SheetResponseEntity(List<Entity> entities, double totalLikes, double totalFollowers) {
        this.entities = entities;
        this.totalLikes = totalLikes;
        this.totalFollowers = totalFollowers;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public double getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(double totalLikes) {
        this.totalLikes = totalLikes;
    }

    public double getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        this.totalFollowers = totalFollowers;
    }
}
