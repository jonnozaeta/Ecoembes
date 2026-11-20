package es.deusto.sd.ecoembes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_measurements")
public class SensorMeasurement {

    @Id
    private String id;
    private String containerId;
    private double fillLevel; 
    private LocalDateTime timestamp;

    public SensorMeasurement() {
    }

    public SensorMeasurement(String id, String containerId, double fillLevel, LocalDateTime timestamp) {
        this.id = id;
        this.containerId = containerId;
        this.fillLevel = fillLevel;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public double getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(double fillLevel) {
        this.fillLevel = fillLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
