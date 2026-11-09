package es.deusto.sd.ecoembes.entity;

public class Plant {
    private String id;
    private String name;
    private String location;
    private double maxDailyCapacityKg;

    public Plant() {
    }

    public Plant(String id, String name, String location, double maxDailyCapacityKg) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxDailyCapacityKg = maxDailyCapacityKg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getMaxDailyCapacityKg() {
        return maxDailyCapacityKg;
    }

    public void setMaxDailyCapacityKg(double maxDailyCapacityKg) {
        this.maxDailyCapacityKg = maxDailyCapacityKg;
    }
}
