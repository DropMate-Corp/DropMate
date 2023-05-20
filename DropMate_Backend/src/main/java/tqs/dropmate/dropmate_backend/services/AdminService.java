package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /** This method returns all the operational statistics of all ACP's */
    public Map<AssociatedCollectionPoint, Map<String, Integer>> getAllACPStatistics() {
        return acpRepository.findAll().stream()
                .collect(Collectors.toMap(
                        acp -> acp,
                        acp -> {

                            Map<String, Integer> statistics = new HashMap<>(acp.getOperationalStatistics());
                            statistics.put("deliveryLimit", acp.getDeliveryLimit());
                            return statistics;
                        }
                ));
    }

    public Map<String, Integer> getSpecificACPStatistics(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = acpRepository.findById(acpID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find ACP with the ID " + acpID + "!"));

        Map<String, Integer> stats = acp.getOperationalStatistics();
        stats.put("deliveryLimit", acp.getDeliveryLimit());

        return stats;
    }

    /** This method returns the details associated with a specific ACP */
    public AssociatedCollectionPoint getACPDetails(Integer acpID) throws ResourceNotFoundException {
        return acpRepository.findById(acpID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find ACP with the ID " + acpID + "!"));
    }
}
