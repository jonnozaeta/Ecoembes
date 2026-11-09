package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.ContainerType;
import es.deusto.sd.ecoembes.entity.SensorMeasurement;
import es.deusto.sd.ecoembes.dto.*;
import es.deusto.sd.ecoembes.external.ContSocketLtdGateway;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    private final ContSocketLtdGateway contSocketGateway;

    // Estructuras de datos en memoria
    private final Map<String, Container> containers = new ConcurrentHashMap<>();
    private final Map<String, List<SensorMeasurement>> measurements = new ConcurrentHashMap<>();

    public ContainerService(ContSocketLtdGateway contSocketGateway) {
        this.contSocketGateway = contSocketGateway;
        seedInitialData();
    }

    private void seedInitialData() {
        // Contenedor de prueba inicial
        Container c1 = new Container("CONT-48001-01", "48001", ContainerType.YELLOW_PLASTIC, 3000.0, 45.0, LocalDateTime.now().minusHours(2));
        Container c2 = new Container("CONT-48001-02", "48001", ContainerType.GREEN_GLASS, 2500.0, 70.0, LocalDateTime.now().minusHours(1));
        Container c3 = new Container("CONT-48002-01", "48002", ContainerType.BLUE_PAPER, 3000.0, 30.0, LocalDateTime.now().minusHours(3));

        containers.put(c1.getId(), c1);
        containers.put(c2.getId(), c2);
        containers.put(c3.getId(), c3);

        List<SensorMeasurement> hist1 = new ArrayList<>();
        hist1.add(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 20.0, LocalDateTime.now().minusDays(2)));
        hist1.add(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 35.0, LocalDateTime.now().minusDays(1)));
        hist1.add(new SensorMeasurement(UUID.randomUUID().toString(), c1.getId(), 45.0, LocalDateTime.now().minusHours(2)));
        measurements.put(c1.getId(), Collections.synchronizedList(hist1));
    }

    public boolean exists(String containerId) {
        return containers.containsKey(containerId);
    }

    public Optional<ContainerDTO> createContainer(CreateContainerDTO dto) {
        if (containers.containsKey(dto.getContainerId())) {
            return Optional.empty(); // Conflict
        }
        Container container = new Container(
                dto.getContainerId(),
                dto.getPostalCode(),
                dto.getType(),
                dto.getCapacity(),
                0.0,
                LocalDateTime.now()
        );
        containers.put(container.getId(), container);
        measurements.put(container.getId(), Collections.synchronizedList(new ArrayList<>()));

        return Optional.of(toDTO(container));
    }

    public Optional<ContainerStatusDTO> processSensorUpdate(SensorUpdateDTO dto) {
        Container container = containers.get(dto.getContainerId());
        if (container == null) {
            return Optional.empty(); // 404
        }

        LocalDateTime timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now();
        container.setCurrentLevel(dto.getCurrentLevel());
        container.setLastUpdated(timestamp);

        // Registra el historial
        SensorMeasurement measurement = new SensorMeasurement(
                UUID.randomUUID().toString(),
                container.getId(),
                dto.getCurrentLevel(),
                timestamp
        );
        measurements.computeIfAbsent(container.getId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(measurement);

        // Notifica al servicio externo ContSocket Ltd.
        contSocketGateway.forwardTelemetry(container.getId(), dto.getCurrentLevel(), timestamp);

        return Optional.of(new ContainerStatusDTO(container.getId(), dto.getCurrentLevel(), timestamp));
    }

    public Optional<List<ContainerStatusDTO>> getUsageHistory(String containerId, LocalDate startDate, LocalDate endDate) {
        if (!containers.containsKey(containerId)) {
            return Optional.empty();
        }

        List<SensorMeasurement> list = measurements.getOrDefault(containerId, Collections.emptyList());
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
        // Agrupa los contenedores por código postal
        Map<String, List<Container>> grouped = containers.values().stream()
                .filter(c -> postalCode == null || postalCode.isBlank() || c.getPostalCode().equalsIgnoreCase(postalCode))
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
