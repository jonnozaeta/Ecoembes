package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Allocation;
import es.deusto.sd.ecoembes.entity.Plant;
import es.deusto.sd.ecoembes.dto.AllocationDTO;
import es.deusto.sd.ecoembes.dto.AllocationResultDTO;
import es.deusto.sd.ecoembes.dto.PlantCapacityDTO;
import es.deusto.sd.ecoembes.external.PlasSBLtdGateway;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlasSBLtdGateway plasSBGateway;
    private final ContainerService containerService;

    private final Map<String, Plant> plants = new ConcurrentHashMap<>();
    private final Map<String, Allocation> allocations = new ConcurrentHashMap<>();

    public PlantService(PlasSBLtdGateway plasSBGateway, ContainerService containerService) {
        this.plasSBGateway = plasSBGateway;
        this.containerService = containerService;
        seedPlants();
    }

    private void seedPlants() {
        Plant p1 = new Plant("PLANT-01", "Planta Bizkaia PlasSB", "Bilbao", 50000.0);
        Plant p2 = new Plant("PLANT-02", "Planta Araba Eco", "Vitoria-Gasteiz", 40000.0);
        Plant p3 = new Plant("PLANT-03", "Planta Gipuzkoa Berria", "Donostia", 45000.0);

        plants.put(p1.getId(), p1);
        plants.put(p2.getId(), p2);
        plants.put(p3.getId(), p3);
    }

    public List<PlantCapacityDTO> getPlantsCapacity(LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();

        return plants.values().stream().map(p -> {
            // Llamada externa a PlasSB Ltd. para obtener los datos actuales de capacidad
            double used = plasSBGateway.getUsedCapacity(p.getId(), queryDate);
            double available = Math.max(0.0, p.getMaxDailyCapacityKg() - used);
            return new PlantCapacityDTO(p.getId(), p.getName(), p.getMaxDailyCapacityKg(), used, available, queryDate);
        }).collect(Collectors.toList());
    }

    public enum AllocationStatus {
        SUCCESS,
        PLANT_NOT_FOUND,
        CONTAINER_NOT_FOUND,
        CAPACITY_EXCEEDED
    }

    public static class AllocationResponse {
        private final AllocationStatus status;
        private final AllocationResultDTO result;

        public AllocationResponse(AllocationStatus status, AllocationResultDTO result) {
            this.status = status;
            this.result = result;
        }

        public AllocationStatus getStatus() {
            return status;
        }

        public AllocationResultDTO getResult() {
            return result;
        }
    }

    public AllocationResponse allocateWaste(String plantId, AllocationDTO dto) {
        Plant plant = plants.get(plantId);
        if (plant == null) {
            return new AllocationResponse(AllocationStatus.PLANT_NOT_FOUND, null);
        }

        if (dto.getContainerId() != null && !dto.getContainerId().isBlank()) {
            if (!containerService.exists(dto.getContainerId())) {
                return new AllocationResponse(AllocationStatus.CONTAINER_NOT_FOUND, null);
            }
        }

        LocalDate allocationDate = dto.getAllocationDate() != null ? dto.getAllocationDate() : LocalDate.now();

        // Comprueba y reserva capacidad con PlasSB Ltd.
        boolean reserved = plasSBGateway.reserveCapacity(plantId, dto.getWasteAmount(), dto.getWasteType(), allocationDate);
        if (!reserved) {
            return new AllocationResponse(AllocationStatus.CAPACITY_EXCEEDED, null);
        }

        String allocationId = "ALLOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Allocation allocation = new Allocation(
                allocationId,
                plantId,
                dto.getContainerId(),
                dto.getWasteAmount(),
                dto.getWasteType(),
                allocationDate.atStartOfDay(),
                "CONFIRMED"
        );
        allocations.put(allocationId, allocation);

        AllocationResultDTO resultDTO = new AllocationResultDTO(
                allocationId,
                plantId,
                dto.getContainerId(),
                dto.getWasteAmount(),
                "CONFIRMED",
                LocalDateTime.now()
        );

        return new AllocationResponse(AllocationStatus.SUCCESS, resultDTO);
    }
}
