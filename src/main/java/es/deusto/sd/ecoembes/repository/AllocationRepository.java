package es.deusto.sd.ecoembes.repository;

import es.deusto.sd.ecoembes.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, String> {
    List<Allocation> findByPlantId(String plantId);
}
