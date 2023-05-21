package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

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

    /** This method returns all the parcels waiting for delivery
     * @return List of all parcels waiting delivery */
    public List<Parcel> getAllParcelsWaitingDelivery(){
        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.IN_DELIVERY))
                .toList();
    }
  
    /** This method returns all the parcels waiting for pickup
     * @return List of all the parcels waiting for pickup.*/
    public List<Parcel> getAllParcelsWaitingPickup(){
        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.WAITING_FOR_PICKUP))
                .toList();
    }

    /** This method returns all the parcels waiting for delivery at a specific ACP
     * @param acpID - ID of the ACP in the database
     * @return List of the parcels waiting delivery at the corresponding ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<Parcel> getParcelsWaitingDeliveryAtACP(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.IN_DELIVERY))
                .filter(parcel -> parcel.getPickupACP().equals(acp))
                .toList();
    }

    /** This method returns all the parcels waiting for pickup at a specific ACP
     * @param acpID - ID of the ACP in the database
     * @return List of the parcels waiting pickup at the corresponding ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<Parcel> getParcelsWaitingPickupAtACP(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.WAITING_FOR_PICKUP))
                .filter(parcel -> parcel.getPickupACP().equals(acp))
                .toList();
    }

    /** This method returns all the operational statistics of all ACP's
     * @return Map containing the operational statistics for each ACP. Each key is a ACP, and the value another Map of the corresponding statistics.
     * */
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

    /** This method returns the details associated with a specific ACP
     * @param acpID - ID of the ACP in the database
     * @return Map containing the operational statistics of the ACP. Each key is a parameter, with the value being the related statistic.
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Map<String, Integer> getSpecificACPStatistics(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        Map<String, Integer> stats = acp.getOperationalStatistics();
        stats.put("deliveryLimit", acp.getDeliveryLimit());

        return stats;
    }

    /** This method returns the details associated with a specific ACP
     * @param acpID - ID of the ACP in the database
     * @return corresponding ACP details
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public AssociatedCollectionPoint getACPDetails(Integer acpID) throws ResourceNotFoundException {
        return this.getACPFromID(acpID);
    }

    /** Updates the details of an ACP
     * @param acpID - ID of the ACP in the database
     * @param email - Updated email for the ACP. Could be null
     * @param name - Updated name for the ACP. Could be null
     * @param telephone - Updated telephone for the ACP. Could be null
     * @param city - Updated city for the ACP. Could be null
     * @param address - Updated address for the ACP. Could be null
     * @return a message stating the Request was Succesful
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public AssociatedCollectionPoint updateACPDetails(Integer acpID, String email, String name, String telephone, String city, String address) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        acp.setName(name != null ? name : acp.getName());
        acp.setEmail(email != null ? email : acp.getEmail());
        acp.setTelephoneNumber(telephone != null ? telephone : acp.getTelephoneNumber());
        acp.setCity(city != null ? city : acp.getCity());
        acp.setAddress(address != null ? address : acp.getAddress());

        acpRepository.save(acp);

        return acp;
    }



    // Auxilliary functions

    /** This method checks whether an ACP exists or not on the ACPRepository, based on its ID. If it doesn't it throws
     * an exception.
     * @param acpID - ID of the ACP in the database
     * @return corresponding ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     */
    private AssociatedCollectionPoint getACPFromID(Integer acpID) throws ResourceNotFoundException {
        return acpRepository.findById(acpID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find ACP with the ID " + acpID + "!"));
    }
}
