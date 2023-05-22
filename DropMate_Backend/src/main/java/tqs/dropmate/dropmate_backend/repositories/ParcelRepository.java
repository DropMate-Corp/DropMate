package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
}
