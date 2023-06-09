package tqs.dropmate.dropmate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.*;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private AssociatedCollectionPointRepository acpRepository;
    private ParcelRepository parcelRepository;
    private PendingAssociatedCollectionPointRepository pendingACPRepository;
    private StoreRepository storeRepository;
    private SystemAdministratorRepository systemAdministratorRepository;

    public AdminService(AssociatedCollectionPointRepository acpRepository, ParcelRepository parcelRepository, PendingAssociatedCollectionPointRepository pendingACPRepository, StoreRepository storeRepository, SystemAdministratorRepository systemAdministratorRepository) {
        this.acpRepository = acpRepository;
        this.parcelRepository = parcelRepository;
        this.pendingACPRepository = pendingACPRepository;
        this.storeRepository = storeRepository;
        this.systemAdministratorRepository = systemAdministratorRepository;
    }

    // ACP Methods
    /** This method returns all the ACP's associated with the Platform */
    public List<AssociatedCollectionPoint> getAllACP(){
        return acpRepository.findAll();
    }

    /** Updates the details of an ACP
     * @param acpID - ID of the ACP in the database
     * @param email - Updated email for the ACP. Could be null
     * @param name - Updated name for the ACP. Could be null
     * @param telephone - Updated telephone for the ACP. Could be null
     * @param city - Updated city for the ACP. Could be null
     * @param address - Updated address for the ACP. Could be null
     * @return the updated ACP
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

    /** This method returns the details associated with a specific ACP
     * @param acpID - ID of the ACP in the database
     * @return corresponding ACP details
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public AssociatedCollectionPoint getACPDetails(Integer acpID) throws ResourceNotFoundException {
        return this.getACPFromID(acpID);
    }

    /** This method deletes an ACP
     * @param acpID - ID of the ACP in the database
     * @return a Successful Request message
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public SuccessfulRequest removeACP(Integer acpID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);

        acpRepository.delete(acp);

        return new SuccessfulRequest("ACP succesfully deleted!");
    }

    /** Method for the DropMate administrator login
     * @param email - The email of the System Admin
     * @param password - The password of the System Admin
     * @return the corresponding System Admin object
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public SystemAdministrator processAdminLogin(String email, String password) throws InvalidCredentialsException {
        SystemAdministrator admin = systemAdministratorRepository.findByEmail(email);

        if(admin != null && admin.getPassword().equals(password)){
            return admin;
        }
        throw new InvalidCredentialsException("Invalid login credentials");
    }



    // E-Store Methods


    /** This method returns all the E-Stores associated with the Platform
     * @return the List of all E-Stores partnered with the platform
     * */
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }


    // Operational Statistics


    /** This method returns all the operational statistics of all ACP's
     * @return Map containing the operational statistics for each ACP. Each key is the ID of the ACP, and the value another Map of the corresponding statistics.
     * */
    public Map<Integer, Map<String, Integer>> getAllACPStatistics() {
        return acpRepository.findAll().stream()
                .collect(Collectors.toMap(
                        AssociatedCollectionPoint::getAcpId,
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


    // Management of Parcels


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


    // Management of Pending ACP's


    /** Adds a new ACP to the pending list
     * @param email - Updated email for the ACP. Could be null
     * @param name - Updated name for the ACP. Could be null
     * @param telephoneNumber - Updated telephone for the ACP. Could be null
     * @param city - Updated city for the ACP. Could be null
     * @param address - Updated address for the ACP. Could be null
     * @param description - A small text explaining why this store wants to be a Partner ACP
     * @return the entity for the new Pending ACP, including its ID
     * */
    public PendingACP addNewPendingAcp(String name, String email, String city, String address, String telephoneNumber, String description){
        PendingACP newCandidateACP = new PendingACP();

        newCandidateACP.setName(name);
        newCandidateACP.setEmail(email);
        newCandidateACP.setCity(city);
        newCandidateACP.setAddress(address);
        newCandidateACP.setTelephoneNumber(telephoneNumber);
        newCandidateACP.setDescription(description);
        newCandidateACP.setStatus(0);

        pendingACPRepository.save(newCandidateACP);

        return pendingACPRepository.findFirstByName(name);
    }

    /** Changes the status of a candidate ACP
     * @param candidateID - ID of the candidate ACP
     * @param newStatus - New status for the candidate ACP. 0 means Pending, 1 means Rejected and 2 means Accepted.
     *                  If accepted, a new related AssociatedCollectionPoint should be created.
     * @return a message stating if the operation was successful
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public SuccessfulRequest changePendingACPStatus(Integer candidateID, Integer newStatus) throws ResourceNotFoundException {
        PendingACP candidateACP = this.getCandidateACPFromID(candidateID);

        if(!candidateACP.getStatus().equals(0)){
            return new SuccessfulRequest("Operation denied, as this candidate request has already been reviewed!");
        }

        candidateACP.setStatus(newStatus);
        pendingACPRepository.save(candidateACP);

        // If the ACP was accepted, we create a new corresponding ACP entity
        if(newStatus.equals(2)){
            AssociatedCollectionPoint newACP = new AssociatedCollectionPoint();

            newACP.setName(candidateACP.getName());
            newACP.setEmail(candidateACP.getEmail());
            newACP.setAddress(candidateACP.getAddress());
            newACP.setTelephoneNumber(candidateACP.getTelephoneNumber());
            newACP.setCity(candidateACP.getCity());

            acpRepository.save(newACP);

            return new SuccessfulRequest("Request accepted!");
        }

        return new SuccessfulRequest("Request rejected!");
    }

    /** This method returns all the Pending ACP's associated with the Platform */
    public List<PendingACP> getAllPendingACP(){
        return pendingACPRepository.findAll();
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

    /** This method checks whether a candidate ACP exists or not on the pendingACPRepository, based on its ID. If it doesn't it throws
     * an exception.
     * @param acpID - ID of the ACP in the database
     * @return corresponding ACP
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     */
    private PendingACP getCandidateACPFromID(Integer acpID) throws ResourceNotFoundException {
        return pendingACPRepository.findById(acpID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find candidate ACP with the ID " + acpID + "!"));
    }
}
