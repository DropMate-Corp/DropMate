package tqs.dropmate.dropmate_backend.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.datamodel.Store;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.repositories.StoreRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class StoreService {
    @Autowired
    AssociatedCollectionPointRepository acpRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ParcelRepository parcelRepository;

    /** Used to post a new order placed by a Client of the partner E-Store
     * @param acpID - ID of the ACP selected by the user in the database
     * @param storeID - ID of the Store the order comes from
     * @return the created Parcel object
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public Parcel createNewOrder(Integer acpID, Integer storeID) throws ResourceNotFoundException {
        AssociatedCollectionPoint acp = this.getACPFromID(acpID);
        Store store = this.getStoreFromID(storeID);

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

        return newOrder;
    }

    /** Gets all of the ACP's available to take new orders, that is, the ACP's currently under their operational limit
     * @param storeID - ID of the Store the order comes from
     * @return a list with all the available ACP's
     * @throws ResourceNotFoundException - Exception raised when an ID doesn't exist in the database
     * */
    public List<AssociatedCollectionPoint> getAvailableACP(Integer storeID) throws ResourceNotFoundException {
        Store store = this.getStoreFromID(storeID);
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

        Random random = new Random();
        StringBuilder code = new StringBuilder();

        // Generate 3 random uppercase letters
        for (int i = 0; i < 3; i++){
            int randomIndex = random.nextInt(letters.length());
            code.append(letters.charAt(randomIndex));
        }

        // Generate 3 random numbers
        for (int i = 0; i < 3; i++){
            int randomIndex = random.nextInt(numbers.length());
            code.append(numbers.charAt(randomIndex));
        }

        return code.toString();
    }
}
