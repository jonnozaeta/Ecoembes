package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.dto.AllocationDTO;
import es.deusto.sd.ecoembes.dto.AllocationResultDTO;
import es.deusto.sd.ecoembes.dto.PlantCapacityDTO;
import es.deusto.sd.ecoembes.service.AuthService;
import es.deusto.sd.ecoembes.service.PlantService;
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

@RestController
@RequestMapping("/api/plants")
@Tag(name = "Plantas de Reciclaje (Facade)", description = "Controlador Facade para capacidad de plantas y asignación de residuos")
public class PlantController {

    private final PlantService plantService;
    private final AuthService authService;

    public PlantController(PlantService plantService, AuthService authService) {
        this.plantService = plantService;
        this.authService = authService;
    }

    @GetMapping("/capacity")
    @Operation(summary = "getPlantsCapacity", description = "Consulta la capacidad total, utilizada y disponible de las plantas de reciclaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capacidades obtenidas correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros incorrectos"),
            @ApiResponse(responseCode = "401", description = "Token no válido o no autorizado")
    })
    public ResponseEntity<List<PlantCapacityDTO>> getPlantsCapacity(
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Fecha a consultar (opcional, formato YYYY-MM-DD)")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PlantCapacityDTO> capacities = plantService.getPlantsCapacity(date);
        return ResponseEntity.ok(capacities);
    }

    @PostMapping("/{plantId}/allocations")
    @Operation(summary = "allocate", description = "Asigna una cantidad de residuo de un contenedor a una planta de tratamiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignación creada y confirmada satisfactoriamente"),
            @ApiResponse(responseCode = "400", description = "Datos de asignación inválidos"),
            @ApiResponse(responseCode = "401", description = "Token no válido o no autorizado"),
            @ApiResponse(responseCode = "404", description = "Planta o contenedor de origen no encontrado"),
            @ApiResponse(responseCode = "409", description = "Capacidad excedida en la planta para la fecha solicitada")
    })
    public ResponseEntity<AllocationResultDTO> allocate(
            @Parameter(description = "ID de la planta receptora", required = true)
            @PathVariable("plantId") String plantId,
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token,
            @Valid @RequestBody AllocationDTO allocationDTO) {

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlantService.AllocationResponse response = plantService.allocateWaste(plantId, allocationDTO);

        switch (response.getStatus()) {
            case SUCCESS:
                return ResponseEntity.status(HttpStatus.CREATED).body(response.getResult());
            case PLANT_NOT_FOUND:
            case CONTAINER_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case CAPACITY_EXCEEDED:
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
