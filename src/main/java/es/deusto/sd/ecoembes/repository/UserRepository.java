package es.deusto.sd.ecoembes.repository;

import es.deusto.sd.ecoembes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
