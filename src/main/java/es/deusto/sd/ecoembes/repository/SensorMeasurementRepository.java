package es.deusto.sd.ecoembes.repository;

import es.deusto.sd.ecoembes.entity.SensorMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorMeasurementRepository extends JpaRepository<SensorMeasurement, String> {
    List<SensorMeasurement> findByContainerIdOrderByTimestampAsc(String containerId);
}
