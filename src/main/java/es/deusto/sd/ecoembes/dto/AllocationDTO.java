package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.ContainerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "Petición para asignar residuos de un contenedor a una planta")
public class AllocationDTO {

    @Schema(description = "Identificador del contenedor de origen", example = "CONT-48001-01")
    private String containerId;

    @Positive(message = "La cantidad de residuo debe ser positiva")
    @Schema(description = "Cantidad de residuo en kilogramos", example = "1250.0")
    private double wasteAmount;

    @NotNull(message = "El tipo de residuo es obligatorio")
    @Schema(description = "Tipo de residuo a procesar", example = "YELLOW_PLASTIC")
    private ContainerType wasteType;

    @Schema(description = "Fecha planificada para la asignación (opcional, por defecto hoy)")
    private LocalDate allocationDate;

    public AllocationDTO() {
    }

    public AllocationDTO(String containerId, double wasteAmount, ContainerType wasteType, LocalDate allocationDate) {
        this.containerId = containerId;
        this.wasteAmount = wasteAmount;
        this.wasteType = wasteType;
        this.allocationDate = allocationDate;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public double getWasteAmount() {
        return wasteAmount;
    }

    public void setWasteAmount(double wasteAmount) {
        this.wasteAmount = wasteAmount;
    }

    public ContainerType getWasteType() {
        return wasteType;
    }

    public void setWasteType(ContainerType wasteType) {
        this.wasteType = wasteType;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }
}
