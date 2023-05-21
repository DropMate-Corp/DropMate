package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.dropmate.dropmate_backend.datamodel.PendingACP;

@Repository
public interface PendingAssociatedCollectionPointRepository extends JpaRepository<PendingACP, Integer> {
    PendingACP findFirstByName(String name);
}
