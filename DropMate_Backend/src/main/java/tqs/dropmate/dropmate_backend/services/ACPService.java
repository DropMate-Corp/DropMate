package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.sql.Date;
import java.time.LocalDate;
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

    /** Used to check-in a parcel when it reaches the Pickup Point
     * @param parcelID - ID of the parcel on the DropMate database
     * @param deliveryCode - Delivery code generated for the parcel
     * @return A message saying the check in process was successful
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Parcel checkInProcess(Integer parcelID, String deliveryCode) throws ResourceNotFoundException, InvalidCredentialsException {
        Parcel parcel = this.getParcelFromID(parcelID);

        // Checking if the delivery code inputted by the Operator is correct
        if(! deliveryCode.equals(parcel.getDeliveryCode())){
            throw new InvalidCredentialsException("Request denied. Delivery code inputted by Operator doesn't match the code of the parcel.");
        }

        // Updating the Parcel status
        parcel.setParcelStatus(Status.WAITING_FOR_PICKUP);
        parcel.setDeliveryDate(Date.valueOf(LocalDate.now()));
        parcelRepository.save(parcel);

        return parcel;
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

    /** This method checks whether a Parcel exists or not on the ParcleRepository, based on its ID. If it doesn't it throws
     * an exception.
     * @param parcelID - ID of the Parcel in the database
     * @return corresponding ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     */
    private Parcel getParcelFromID(Integer parcelID) throws ResourceNotFoundException {
        return parcelRepository.findById(parcelID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find ACP with the ID " + parcelID + "!"));
    }
}
