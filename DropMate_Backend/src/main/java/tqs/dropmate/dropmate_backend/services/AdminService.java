package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private AssociatedCollectionPointRepository acpRepository;
    @Autowired
    private ParcelRepository parcelRepository;

    /** This method returns all the ACP's associated with the Platform */
    public List<AssociatedCollectionPoint> getAllACP(){
        return acpRepository.findAll();
    }

    /** This method returns all the parcels waiting for delivery */
    public List<Parcel> getAllParcelsWaitingDelivery(){
        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.IN_DELIVERY))
                .collect(Collectors.toList());
    }

    /** This method returns all the parcels waiting for pickup */
    public List<Parcel> getAllParcelsWaitingPickup(){
        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.WAITING_FOR_PICKUP))
                .collect(Collectors.toList());
    }
}
