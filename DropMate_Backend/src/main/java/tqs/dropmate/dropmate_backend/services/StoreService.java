package tqs.dropmate.dropmate_backend.services;

import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.datamodel.Store;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.repositories.StoreRepository;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class StoreService {

    AssociatedCollectionPointRepository acpRepository;
    StoreRepository storeRepository;
    ParcelRepository parcelRepository;

    public StoreService(AssociatedCollectionPointRepository acpRepository, StoreRepository storeRepository, ParcelRepository parcelRepository) {
        this.acpRepository = acpRepository;
        this.storeRepository = storeRepository;
        this.parcelRepository = parcelRepository;
    }

    /** Used to post a new order placed by a Client of the partner E-Store
     * @param acpID - ID of the ACP selected by the user in the database
     * @param storeID - ID of the Store the order comes from
     * @return the created Parcel object
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Map<String, String> createNewOrder(Integer acpID, Integer storeID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);
        Store store = this.getStoreFromID(storeID);
        Map<String, String> returnMap = new HashMap<>();

        Parcel newOrder = new Parcel();

        // Set the store and the ACP
        newOrder.setPickupACP(acp);
        newOrder.setStore(store);

        // Set other required information for the Parcel
        newOrder.setWeight(5.0);
        newOrder.setDeliveryCode(this.generateRandomCode());
        newOrder.setPickupCode(this.generateRandomCode());
        newOrder.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(5)));
        newOrder.setParcelStatus(Status.IN_DELIVERY);

        // Saving the new entity on the DB
        parcelRepository.save(newOrder);

        // Returning the required information to the store
        returnMap.put("status", newOrder.getParcelStatus().toString());
        returnMap.put("delivery_date", newOrder.getDeliveryDate().toString());
        returnMap.put("pickup_code", newOrder.getPickupCode());

        return returnMap;
    }

    /** Gets all of the ACP's available to take new orders, that is, the ACP's currently under their operational limit
     * @param storeID - ID of the Store the order comes from
     * @return a list with all the available ACP's
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<AssociatedCollectionPoint> getAvailableACP(Integer storeID) throws ResourceNotFoundException {
        this.getStoreFromID(storeID);
        List<AssociatedCollectionPoint> avaliableACP = new ArrayList<>();

        for(AssociatedCollectionPoint acp:acpRepository.findAll()){
            int parcelsInDelivery = acp.getOperationalStatistics().getOrDefault("parcels_in_delivery", 0);
            int parcelsWaitingPickup = acp.getOperationalStatistics().getOrDefault("parcels_waiting_pickup", 0);
            int limit = acp.getDeliveryLimit();

            if(parcelsInDelivery + parcelsWaitingPickup < limit) {
                avaliableACP.add(acp);
            }
        }

        return avaliableACP;
    }

    /** Returns the current status of a parcel, as well as it's delivery/pickup date, if available. These are returned in a Map.
     The input is the delivery code of the parcel.
     * @param pickupCode - Pickup Code of the Parcel, sent by the E-Store
     * @return a map containing the current status of the parcel, as well as the delivery date and the pickup date, if they aren't null
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Map<String, String> getParcelStatus(String pickupCode) throws ResourceNotFoundException {
        Parcel parcel = parcelRepository.findFirstByPickupCode(pickupCode);

        if(parcel == null){
            throw new ResourceNotFoundException("Couldn't find Parcel with the pickup Code " + pickupCode + "!");
        }

        Map<String, String> returnMap = new HashMap<>();

        returnMap.put("status", parcel.getParcelStatus().toString());

        returnMap.put("delivery_date", Optional.ofNullable(parcel.getDeliveryDate())
                .map(Date::toString)
                .orElse(null));

        returnMap.put("pickup_date", Optional.ofNullable(parcel.getPickupDate())
                .map(Date::toString)
                .orElse(null));

        return returnMap;
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

    /** This method checks whether a store exists or not on the StoreRepository, based on its ID. If it doesn't it throws
     * an exception.
     * @param storeID - ID of the store in the database
     * @return corresponding Store
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     */
    private Store getStoreFromID(Integer storeID) throws ResourceNotFoundException {
        return storeRepository.findById(storeID).orElseThrow(() -> new ResourceNotFoundException("Couldn't find Store with the ID " + storeID + "!"));
    }

    /** This method generates a random String code containing 3 uppercase letters and 3 numbers. It's used on this case
     * to create the DeliveryCode and PickupCode of a parcel.
     * @return the code
     */
    public String generateRandomCode(){
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        // Generate 4 random uppercase letters
        for (int i = 0; i < 4; i++){
            int randomIndex = random.nextInt(letters.length());
            code.append(letters.charAt(randomIndex));
        }

        // Generate 4 random numbers
        for (int i = 0; i < 4; i++){
            int randomIndex = random.nextInt(numbers.length());
            code.append(numbers.charAt(randomIndex));
        }

        // If the code currently exists in the DB, generate another code instead
        if(parcelRepository.existsByPickupCode(code.toString())){
            return this.generateRandomCode();
        }

        return code.toString();
    }
}
