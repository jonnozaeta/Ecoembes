package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resultado tras procesar la asignación de residuo a una planta")
public class AllocationResultDTO {

    @Schema(description = "Identificador de la asignación creada", example = "ALLOC-9812")
    private String allocationId;

    @Schema(description = "Identificador de la planta asignada", example = "PLANT-01")
    private String plantId;

    @Schema(description = "Identificador del contenedor origen", example = "CONT-48001-01")
    private String containerId;

    @Schema(description = "Cantidad asignada en kg", example = "1250.0")
    private double allocatedAmount;

    @Schema(description = "Estado de la asignación", example = "CONFIRMED")
    private String status;

    @Schema(description = "Fecha y hora del registro")
    private LocalDateTime timestamp;

    public AllocationResultDTO() {
    }

    public AllocationResultDTO(String allocationId, String plantId, String containerId, double allocatedAmount, String status, LocalDateTime timestamp) {
        this.allocationId = allocationId;
        this.plantId = plantId;
        this.containerId = containerId;
        this.allocatedAmount = allocatedAmount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
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

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
