package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ACPService {

    @Autowired
    private AssociatedCollectionPointRepository acpRepository;
    @Autowired
    private ParcelRepository parcelRepository;

    /** Returns the current Delivery Limit for the ACP
     * @param acpID - ID of the ACP in the database
     * @return The delivery limit of the ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Integer getDeliveryLimit(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return acp.getDeliveryLimit();
    }

    /** Updates the Delivery Limit for the ACP
     * @param acpID - ID of the ACP in the database
     * @return The updated delivery limit of the ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Integer updateDeliveryLimit(Integer acpID, Integer deliveryLimit) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        acp.setDeliveryLimit(deliveryLimit);
        acpRepository.save(acp);

        return deliveryLimit;
    }

    /** Get all parcels belonging to the ACP in the "In delivery" state
     * @param acpID - ID of the ACP in the database
     * @return The list of parcels waiting for delivery at this specific ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<Parcel> getAllParcelsWaitingDelivery(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.IN_DELIVERY))
                .filter(parcel -> parcel.getPickupACP().equals(acp))
                .toList();
    }

    /** Get all parcels belonging to the ACP in the "Waiting for pickup" state
     * @param acpID - ID of the ACP in the database
     * @return The list of parcels waiting for pickup at this specific ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<Parcel> getAllParcelsWaitingForPickup(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.WAITING_FOR_PICKUP))
                .filter(parcel -> parcel.getPickupACP().equals(acp))
                .toList();
    }

    /** Get all parcels belonging to the ACP in the "Delivered" state
     * @param acpID - ID of the ACP in the database
     * @return The list of parcels delivered at this specific ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<Parcel> getAllParcelsDelivered(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        return parcelRepository.findAll().stream()
                .filter(parcel -> parcel.getParcelStatus().equals(Status.DELIVERED))
                .filter(parcel -> parcel.getPickupACP().equals(acp))
                .toList();
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
