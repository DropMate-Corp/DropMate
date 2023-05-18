package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AssociatedCollectionPointRepository acpRepository;
    @Autowired
    private ParcelRepository parcelRepository;

    public List<AssociatedCollectionPoint> getAllACP(){
        return acpRepository.findAll();
    }

    public List<Parcel> getAllParcelsWaitingDelivery(){return null;}
}
