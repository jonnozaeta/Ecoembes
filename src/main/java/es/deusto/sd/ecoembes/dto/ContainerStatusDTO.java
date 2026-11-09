package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Estado o histórico de nivel de un contenedor")
public class ContainerStatusDTO {

    @Schema(description = "Identificador del contenedor", example = "CONT-48001-01")
    private String containerId;

    @Schema(description = "Nivel de llenado (%)", example = "65.0")
    private double level;

    @Schema(description = "Fecha y hora del registro")
    private LocalDateTime timestamp;

    public ContainerStatusDTO() {
    }

    public ContainerStatusDTO(String containerId, double level, LocalDateTime timestamp) {
        this.containerId = containerId;
        this.level = level;
        this.timestamp = timestamp;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
