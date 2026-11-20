package es.deusto.sd.ecoembes.repository;

import es.deusto.sd.ecoembes.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepository extends JpaRepository<Plant, String> {
}
