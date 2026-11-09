package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.dto.*;
import es.deusto.sd.ecoembes.service.AuthService;
import es.deusto.sd.ecoembes.service.ContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/containers")
@Tag(name = "Contenedores (Facade)", description = "Controlador Facade para la gestión de contenedores y sensores")
public class ContainerController {

    private final ContainerService containerService;
    private final AuthService authService;

    public ContainerController(ContainerService containerService, AuthService authService) {
        this.containerService = containerService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "createContainer", description = "Crea y da de alta un nuevo contenedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contenedor creado satisfactoriamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos o faltantes"),
            @ApiResponse(responseCode = "401", description = "Token no válido o no autorizado"),
            @ApiResponse(responseCode = "409", description = "El contenedor ya existe con ese ID")
    })
    public ResponseEntity<ContainerDTO> createContainer(
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token,
            @Valid @RequestBody CreateContainerDTO createDTO) {

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (containerService.exists(createDTO.getContainerId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Optional<ContainerDTO> created = containerService.createContainer(createDTO);
        return created.map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("/sensor")
    @Operation(summary = "sensorUpdate", description = "Recibe la telemetría periódica de llenado de un sensor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lectura del sensor registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Lectura no válida"),
            @ApiResponse(responseCode = "404", description = "Contenedor no encontrado")
    })
    public ResponseEntity<ContainerStatusDTO> sensorUpdate(@Valid @RequestBody SensorUpdateDTO sensorDTO) {
        if (!containerService.exists(sensorDTO.getContainerId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<ContainerStatusDTO> status = containerService.processSensorUpdate(sensorDTO);
        return status.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping("/{containerId}/usage")
    @Operation(summary = "getContainerUsage", description = "Obtiene el histórico de uso/llenado de un contenedor específico en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico devuelto correctamente"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas erróneo"),
            @ApiResponse(responseCode = "401", description = "Token no válido"),
            @ApiResponse(responseCode = "404", description = "Contenedor no encontrado")
    })
    public ResponseEntity<List<ContainerStatusDTO>> getContainerUsage(
            @Parameter(description = "ID del contenedor", required = true)
            @PathVariable("containerId") String containerId,
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = false)
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = false)
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<List<ContainerStatusDTO>> usage = containerService.getUsageHistory(containerId, startDate, endDate);
        return usage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    @Operation(summary = "getZoneStatus", description = "Consulta el estado y porcentaje de llenado de los contenedores por zona/código postal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de zona devuelto"),
            @ApiResponse(responseCode = "400", description = "Parámetros incorrectos"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    public ResponseEntity<List<ContainerZoneStatusDTO>> getZoneStatus(
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Código postal (opcional)")
            @RequestParam(value = "postalCode", required = false) String postalCode,
            @Parameter(description = "Fecha de consulta (opcional)")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ContainerZoneStatusDTO> list = containerService.getZoneStatus(postalCode, date);
        return ResponseEntity.ok(list);
    }
}
