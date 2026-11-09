package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.ContainerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para crear un nuevo contenedor")
public class CreateContainerDTO {

    @NotBlank(message = "El identificador del contenedor no puede estar vacío")
    @Schema(description = "Identificador único del contenedor", example = "CONT-48001-01")
    private String containerId;

    @NotBlank(message = "El código postal es obligatorio")
    @Schema(description = "Código postal de ubicación", example = "48001")
    private String postalCode;

    @NotNull(message = "El tipo de contenedor es obligatorio")
    @Schema(description = "Tipo de residuo que admite", example = "YELLOW_PLASTIC")
    private ContainerType type;

    @Positive(message = "La capacidad debe ser positiva")
    @Schema(description = "Capacidad total en litros", example = "3000.0")
    private double capacity;

    public CreateContainerDTO() {
    }

    public CreateContainerDTO(String containerId, String postalCode, ContainerType type, double capacity) {
        this.containerId = containerId;
        this.postalCode = postalCode;
        this.type = type;
        this.capacity = capacity;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
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
}
