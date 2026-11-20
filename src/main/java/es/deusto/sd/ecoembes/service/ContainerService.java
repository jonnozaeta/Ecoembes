package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.ContainerType;
import es.deusto.sd.ecoembes.entity.SensorMeasurement;
import es.deusto.sd.ecoembes.dto.*;
import es.deusto.sd.ecoembes.repository.ContainerRepository;
import es.deusto.sd.ecoembes.repository.SensorMeasurementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final SensorMeasurementRepository measurementRepository;

    public ContainerService(ContainerRepository containerRepository,
                            SensorMeasurementRepository measurementRepository) {
        this.containerRepository = containerRepository;
        this.measurementRepository = measurementRepository;
    }

    @PostConstruct
    public void seedInitialData() {
        if (containerRepository.count() == 0) {
            Container c1 = new Container("CONT-48001-01", "48001", ContainerType.YELLOW_PLASTIC, 3000.0, 45.0, LocalDateTime.now().minusHours(2));
            Container c2 = new Container("CONT-48001-02", "48001", ContainerType.GREEN_GLASS, 2500.0, 70.0, LocalDateTime.now().minusHours(1));
            Container c3 = new Container("CONT-48002-01", "48002", ContainerType.BLUE_PAPER, 3000.0, 30.0, LocalDateTime.now().minusHours(3));

            containerRepository.saveAll(List.of(c1, c2, c3));

            measurementRepository.save(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 20.0, LocalDateTime.now().minusDays(2)));
            measurementRepository.save(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 35.0, LocalDateTime.now().minusDays(1)));
            measurementRepository.save(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 45.0, LocalDateTime.now().minusHours(2)));
        }
    }

    public boolean exists(String containerId) {
        return containerRepository.existsById(containerId);
    }

    public Optional<ContainerDTO> createContainer(CreateContainerDTO dto) {
        if (containerRepository.existsById(dto.getContainerId())) {
            return Optional.empty(); // 409 Conflict
        }
        Container container = new Container(
                dto.getContainerId(),
                dto.getPostalCode(),
                dto.getType(),
                dto.getCapacity(),
                0.0,
                LocalDateTime.now()
        );
        Container saved = containerRepository.save(container);
        return Optional.of(toDTO(saved));
    }

    public Optional<ContainerStatusDTO> processSensorUpdate(SensorUpdateDTO dto) {
        Optional<Container> containerOpt = containerRepository.findById(dto.getContainerId());
        if (containerOpt.isEmpty()) {
            return Optional.empty(); // 404 Not Found
        }

        Container container = containerOpt.get();
        LocalDateTime timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now();
        container.setCurrentLevel(dto.getCurrentLevel());
        container.setLastUpdated(timestamp);
        containerRepository.save(container);

        // Record measurement history
        SensorMeasurement measurement = new SensorMeasurement(
                UUID.randomUUID().toString(),
                container.getId(),
                dto.getCurrentLevel(),
                timestamp
        );
        measurementRepository.save(measurement);

        return Optional.of(new ContainerStatusDTO(container.getId(), dto.getCurrentLevel(), timestamp));
    }

    public Optional<List<ContainerStatusDTO>> getUsageHistory(String containerId, LocalDate startDate, LocalDate endDate) {
        if (!containerRepository.existsById(containerId)) {
            return Optional.empty();
        }

        List<SensorMeasurement> list = measurementRepository.findByContainerIdOrderByTimestampAsc(containerId);
        List<ContainerStatusDTO> result = list.stream()
                .filter(m -> {
                    LocalDate date = m.getTimestamp().toLocalDate();
                    boolean afterStart = (startDate == null) || !date.isBefore(startDate);
                    boolean beforeEnd = (endDate == null) || !date.isAfter(endDate);
                    return afterStart && beforeEnd;
                })
                .map(m -> new ContainerStatusDTO(m.getContainerId(), m.getFillLevel(), m.getTimestamp()))
                .collect(Collectors.toList());

        return Optional.of(result);
    }

    public List<ContainerZoneStatusDTO> getZoneStatus(String postalCode, LocalDate date) {
        List<Container> allContainers = (postalCode != null && !postalCode.isBlank())
                ? containerRepository.findByPostalCode(postalCode)
                : containerRepository.findAll();

        Map<String, List<Container>> grouped = allContainers.stream()
                .collect(Collectors.groupingBy(Container::getPostalCode));

        List<ContainerZoneStatusDTO> zoneStatuses = new ArrayList<>();
        for (Map.Entry<String, List<Container>> entry : grouped.entrySet()) {
            String pc = entry.getKey();
            List<ContainerDTO> containerDTOs = entry.getValue().stream().map(this::toDTO).collect(Collectors.toList());
            double avg = entry.getValue().stream().mapToDouble(Container::getCurrentLevel).average().orElse(0.0);
            zoneStatuses.add(new ContainerZoneStatusDTO(pc, entry.getValue().size(), Math.round(avg * 100.0) / 100.0, containerDTOs));
        }

        return zoneStatuses;
    }

    private ContainerDTO toDTO(Container c) {
        return new ContainerDTO(
                c.getId(),
                c.getPostalCode(),
                c.getType(),
                c.getCapacity(),
                c.getCurrentLevel(),
                c.getLastUpdated()
        );
    }
}
