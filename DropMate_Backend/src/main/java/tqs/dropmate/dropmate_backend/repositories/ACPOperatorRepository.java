package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.dropmate.dropmate_backend.datamodel.ACPOperator;


@Repository
public interface ACPOperatorRepository extends JpaRepository<ACPOperator, Integer> {
    public ACPOperator findByEmail(String email);
}
