package es.deusto.sd.ecoembes.entity;

import java.time.LocalDateTime;

public class Allocation {
    private String id;
    private String plantId;
    private String containerId;
    private double wasteAmountKg;
    private ContainerType wasteType;
    private LocalDateTime allocationDate;
    private String status; // "CONFIRMED", "PENDING", "REJECTED"

    public Allocation() {
    }

    public Allocation(String id, String plantId, String containerId, double wasteAmountKg, ContainerType wasteType, LocalDateTime allocationDate, String status) {
        this.id = id;
        this.plantId = plantId;
        this.containerId = containerId;
        this.wasteAmountKg = wasteAmountKg;
        this.wasteType = wasteType;
        this.allocationDate = allocationDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public double getWasteAmountKg() {
        return wasteAmountKg;
    }

    public void setWasteAmountKg(double wasteAmountKg) {
        this.wasteAmountKg = wasteAmountKg;
    }

    public ContainerType getWasteType() {
        return wasteType;
    }

    public void setWasteType(ContainerType wasteType) {
        this.wasteType = wasteType;
    }

    public LocalDateTime getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDateTime allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
