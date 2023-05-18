package tqs.dropmate.dropmate_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
}
