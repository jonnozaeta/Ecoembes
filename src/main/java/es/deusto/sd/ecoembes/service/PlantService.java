package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Allocation;
import es.deusto.sd.ecoembes.entity.Plant;
import es.deusto.sd.ecoembes.dto.AllocationDTO;
import es.deusto.sd.ecoembes.dto.AllocationResultDTO;
import es.deusto.sd.ecoembes.dto.PlantCapacityDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final ContainerService containerService;

    private final Map<String, Plant> plants = new ConcurrentHashMap<>();
    private final Map<String, Allocation> allocations = new ConcurrentHashMap<>();

    // Capacidad diaria ya utilizada por planta (plantId -> kg), simulada en memoria
    private final Map<String, Double> usedCapacityKg = new ConcurrentHashMap<>();

    public PlantService(ContainerService containerService) {
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

        // Reservas iniciales simuladas de capacidad
        usedCapacityKg.put("PLANT-01", 32000.0);
        usedCapacityKg.put("PLANT-02", 15000.0);
    }

    // synchronized: evita que dos reservas simultáneas superen juntas el límite
    private synchronized boolean reserveCapacity(String plantId, double weightKg, double maxCapacityKg) {
        double current = usedCapacityKg.getOrDefault(plantId, 0.0);
        if (current + weightKg > maxCapacityKg) {
            return false;
        }
        usedCapacityKg.put(plantId, current + weightKg);
        return true;
    }

    public List<PlantCapacityDTO> getPlantsCapacity(LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();

        return plants.values().stream().map(p -> {
            double used = usedCapacityKg.getOrDefault(p.getId(), 0.0);
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

        // Comprueba y reserva capacidad de la planta
        boolean reserved = reserveCapacity(plantId, dto.getWasteAmount(), plant.getMaxDailyCapacityKg());
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
