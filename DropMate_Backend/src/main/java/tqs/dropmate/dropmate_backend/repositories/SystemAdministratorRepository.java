package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.dropmate.dropmate_backend.datamodel.SystemAdministrator;

@Repository
public interface SystemAdministratorRepository extends JpaRepository<SystemAdministrator, Integer> {
    public SystemAdministrator findByEmail(String email);
}
