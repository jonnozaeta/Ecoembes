package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Lectura de nivel enviada por el sensor del contenedor")
public class SensorUpdateDTO {

    @NotBlank(message = "El identificador del contenedor es obligatorio")
    @Schema(description = "Identificador del contenedor", example = "CONT-48001-01")
    private String containerId;

    @NotNull(message = "El nivel no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El nivel no puede ser menor a 0%")
    @DecimalMax(value = "100.0", message = "El nivel no puede superar el 100%")
    @Schema(description = "Porcentaje de llenado medido (0 - 100)", example = "78.4")
    private Double currentLevel;

    @Schema(description = "Fecha y hora de la lectura (opcional, por defecto fecha actual)")
    private LocalDateTime timestamp;

    public SensorUpdateDTO() {
    }

    public SensorUpdateDTO(String containerId, Double currentLevel, LocalDateTime timestamp) {
        this.containerId = containerId;
        this.currentLevel = currentLevel;
        this.timestamp = timestamp;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Double getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Double currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
