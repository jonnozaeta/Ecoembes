package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Estado agregado de los contenedores de una zona o código postal")
public class ContainerZoneStatusDTO {

    @Schema(description = "Código postal de la zona", example = "48001")
    private String postalCode;

    @Schema(description = "Número total de contenedores en la zona", example = "5")
    private int containerCount;

    @Schema(description = "Nivel medio de llenado en la zona (%)", example = "58.2")
    private double averageFillLevel;

    @Schema(description = "Listado de contenedores en la zona")
    private List<ContainerDTO> containers;

    public ContainerZoneStatusDTO() {
    }

    public ContainerZoneStatusDTO(String postalCode, int containerCount, double averageFillLevel, List<ContainerDTO> containers) {
        this.postalCode = postalCode;
        this.containerCount = containerCount;
        this.averageFillLevel = averageFillLevel;
        this.containers = containers;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getContainerCount() {
        return containerCount;
    }

    public void setContainerCount(int containerCount) {
        this.containerCount = containerCount;
    }

    public double getAverageFillLevel() {
        return averageFillLevel;
    }

    public void setAverageFillLevel(double averageFillLevel) {
        this.averageFillLevel = averageFillLevel;
    }

    public List<ContainerDTO> getContainers() {
        return containers;
    }

    public void setContainers(List<ContainerDTO> containers) {
        this.containers = containers;
    }
}
