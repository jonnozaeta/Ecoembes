package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.ContainerType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Información completa de un contenedor")
public class ContainerDTO {

    @Schema(description = "Identificador del contenedor", example = "CONT-48001-01")
    private String id;

    @Schema(description = "Código postal de la zona", example = "48001")
    private String postalCode;

    @Schema(description = "Tipo de contenedor", example = "YELLOW_PLASTIC")
    private ContainerType type;

    @Schema(description = "Capacidad total", example = "3000.0")
    private double capacity;

    @Schema(description = "Nivel de llenado actual (porcentaje 0-100)", example = "45.5")
    private double currentLevel;

    @Schema(description = "Fecha y hora de última actualización")
    private LocalDateTime lastUpdated;

    public ContainerDTO() {
    }

    public ContainerDTO(String id, String postalCode, ContainerType type, double capacity, double currentLevel, LocalDateTime lastUpdated) {
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
