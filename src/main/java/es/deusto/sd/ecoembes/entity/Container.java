package es.deusto.sd.ecoembes.entity;

import java.time.LocalDateTime;

public class Container {
    private String id;
    private String postalCode;
    private ContainerType type;
    private double capacity; 
    private double currentLevel; 
    private LocalDateTime lastUpdated;

    public Container() {
    }

    public Container(String id, String postalCode, ContainerType type, double capacity, double currentLevel, LocalDateTime lastUpdated) {
        this.id = id;
        this.postalCode = postalCode;
        this.type = type;
        this.capacity = capacity;
        this.currentLevel = currentLevel;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public ContainerType getType() {
        return type;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
