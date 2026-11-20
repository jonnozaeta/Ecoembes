package es.deusto.sd.ecoembes.repository;

import es.deusto.sd.ecoembes.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, String> {
    List<Container> findByPostalCode(String postalCode);
}
