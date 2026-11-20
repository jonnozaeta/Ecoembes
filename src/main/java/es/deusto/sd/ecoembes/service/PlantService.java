package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Allocation;
import es.deusto.sd.ecoembes.entity.Plant;
import es.deusto.sd.ecoembes.dto.AllocationDTO;
import es.deusto.sd.ecoembes.dto.AllocationResultDTO;
import es.deusto.sd.ecoembes.dto.PlantCapacityDTO;
import es.deusto.sd.ecoembes.repository.AllocationRepository;
import es.deusto.sd.ecoembes.repository.PlantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final AllocationRepository allocationRepository;
    private final ContainerService containerService;

    // Capacidad diaria ya utilizada por planta (plantId -> kg)
    private final Map<String, Double> usedCapacityKg = new ConcurrentHashMap<>();

    public PlantService(PlantRepository plantRepository,
                        AllocationRepository allocationRepository,
                        ContainerService containerService) {
        this.plantRepository = plantRepository;
        this.allocationRepository = allocationRepository;
        this.containerService = containerService;
    }

    @PostConstruct
    public void seedPlants() {
        if (plantRepository.count() == 0) {
            Plant p1 = new Plant("PLANT-01", "Planta Bizkaia PlasSB", "Bilbao", 50000.0);
            Plant p2 = new Plant("PLANT-02", "Planta Araba Eco", "Vitoria-Gasteiz", 40000.0);
            Plant p3 = new Plant("PLANT-03", "Planta Gipuzkoa Berria", "Donostia", 45000.0);

            plantRepository.saveAll(List.of(p1, p2, p3));

            usedCapacityKg.put("PLANT-01", 32000.0);
            usedCapacityKg.put("PLANT-02", 15000.0);
        }
    }

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

        return plantRepository.findAll().stream().map(p -> {
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
        Optional<Plant> plantOpt = plantRepository.findById(plantId);
        if (plantOpt.isEmpty()) {
            return new AllocationResponse(AllocationStatus.PLANT_NOT_FOUND, null);
        }
        Plant plant = plantOpt.get();

        if (dto.getContainerId() != null && !dto.getContainerId().isBlank()) {
            if (!containerService.exists(dto.getContainerId())) {
                return new AllocationResponse(AllocationStatus.CONTAINER_NOT_FOUND, null);
            }
        }

        LocalDate allocationDate = dto.getAllocationDate() != null ? dto.getAllocationDate() : LocalDate.now();

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
        allocationRepository.save(allocation);

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
