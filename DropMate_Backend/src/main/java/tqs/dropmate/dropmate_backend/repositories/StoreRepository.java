package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.dropmate.dropmate_backend.datamodel.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}
