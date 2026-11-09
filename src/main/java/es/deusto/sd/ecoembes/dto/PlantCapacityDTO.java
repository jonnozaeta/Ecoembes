package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Capacidad disponible y utilizada de una planta de reciclaje")
public class PlantCapacityDTO {

    @Schema(description = "Identificador de la planta", example = "PLANT-01")
    private String plantId;

    @Schema(description = "Nombre de la planta", example = "Planta Bizkaia PlasSB")
    private String name;

    @Schema(description = "Capacidad máxima diaria (kg)", example = "50000.0")
    private double totalCapacity;

    @Schema(description = "Capacidad ya ocupada (kg)", example = "32000.0")
    private double usedCapacity;

    @Schema(description = "Capacidad libre disponible (kg)", example = "18000.0")
    private double availableCapacity;

    @Schema(description = "Fecha de referencia consultada")
    private LocalDate date;

    public PlantCapacityDTO() {
    }

    public PlantCapacityDTO(String plantId, String name, double totalCapacity, double usedCapacity, double availableCapacity, LocalDate date) {
        this.plantId = plantId;
        this.name = name;
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
        this.availableCapacity = availableCapacity;
        this.date = date;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(double totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(double availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
