package tqs.dropmate.dropmate_backend.serviceTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.datamodel.Store;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.*;
import tqs.dropmate.dropmate_backend.services.StoreService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreService_UnitTest {
    @Mock(lenient = true)
    private StoreRepository storeRepository;
    @Mock(lenient = true)
    private AssociatedCollectionPointRepository acpRepository;
    @Mock(lenient = true)
    private ParcelRepository parcelRepository;


    @InjectMocks
    private StoreService storeService;

    // Expectations
    private Store testStore;
    private List<AssociatedCollectionPoint> allACP;

    @BeforeEach
    public void setUp(){
        testStore = new Store("Store One", "one@mail.pt", "Aveiro", "Fake Adress 1, Aveiro", "000000000");

        AssociatedCollectionPoint pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setName("Pickup One");
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");

        AssociatedCollectionPoint pickupPointTwo = new AssociatedCollectionPoint();
        pickupPointTwo.setName("Pickup Two");
        pickupPointTwo.setCity("Porto");
        pickupPointTwo.setAddress("Fake address 2, Porto");
        pickupPointTwo.setEmail("pickuptwo@mail.pt");
        pickupPointTwo.setDeliveryLimit(15);
        pickupPointTwo.setTelephoneNumber("939333594");

        AssociatedCollectionPoint pickupPointThree = new AssociatedCollectionPoint();
        pickupPointThree.setName("Pickup Three");
        pickupPointThree.setCity("Viseu");
        pickupPointThree.setAddress("Fake address 3, Viseu");
        pickupPointThree.setEmail("pickupthree@mail.pt");
        pickupPointThree.setDeliveryLimit(12);
        pickupPointThree.setTelephoneNumber("900000000");

        // Under Limit
        Map<String, Integer> statsMapOne = new HashMap<>();
        statsMapOne.put("total_parcels", 10);
        statsMapOne.put("parcels_in_delivery", 5);
        statsMapOne.put("parcels_waiting_pickup", 3);
        pickupPointOne.setOperationalStatistics(statsMapOne);

        // Over Limit
        Map<String, Integer> statsMapTwo = new HashMap<>();
        statsMapTwo.put("total_parcels", 30);
        statsMapTwo.put("parcels_in_delivery", 12);
        statsMapTwo.put("parcels_waiting_pickup", 3);
        pickupPointTwo.setOperationalStatistics(statsMapTwo);

        // Under Limit
        Map<String, Integer> statsMapThree = new HashMap<>();
        statsMapThree.put("total_parcels", 30);
        statsMapThree.put("parcels_in_delivery", 2);
        statsMapThree.put("parcels_waiting_pickup", 1);
        pickupPointThree.setOperationalStatistics(statsMapThree);

        // Adding to ACP list
        allACP = new ArrayList<>();
        allACP.add(pickupPointOne);
        allACP.add(pickupPointTwo);
        allACP.add(pickupPointThree);

    }

    @AfterEach
    public void tearDown(){

    }

    @Test
    void whenCreatingOrder_withValidParameters_thenOrderIsCreated() throws ResourceNotFoundException {
        // Set up Expectations
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testStore));
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(allACP.get(0)));

        // Verify the result is as expected
        Parcel order = storeService.createNewOrder(1,1);
        assertThat(order).extracting(Parcel::getPickupACP).isEqualTo(allACP.get(0));
        assertThat(order).extracting(Parcel::getStore).isEqualTo(testStore);
        assertThat(order).extracting(Parcel::getParcelStatus).isEqualTo(Status.IN_DELIVERY);
        assertThat(order).extracting(Parcel::getPickupCode).isNotNull();
        assertThat(order).extracting(Parcel::getDeliveryCode).isNotNull();
        assertThat(order).extracting(Parcel::getDeliveryDate).isNotNull();

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
        this.verifyStoreFindByIdIsCalled();
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).save(Mockito.any());
    }

    @Test
    void whenCreatingOrder_withInvalidStoreID_thenExceptionThrown(){
        // Set up Expectations
        when(storeRepository.findById(-5)).thenReturn(Optional.empty());
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(allACP.get(0)));

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            storeService.createNewOrder(1, -5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find Store with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
        this.verifyStoreFindByIdIsCalled();
    }

    @Test
    void whenCreatingOrder_withInvalidACPID_thenExceptionThrown(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            storeService.createNewOrder(-5, 1);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void testGenerateRandomCode(){
        // Verify the result is as expected
        assertThat(storeService.generateRandomCode()).matches("[A-Z]{3}\\d{3}");
    }

    @Test
    void whenGettingAvailableACP_withValidParameters_thenReturnOnlyACPSUnderLimit() throws ResourceNotFoundException {
        // Set up Expectations
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testStore));
        when(acpRepository.findAll()).thenReturn(allACP);

        // Verify the result is as expected
        List<AssociatedCollectionPoint> availableACP = storeService.getAvailableACP(1);
        assertThat(availableACP).hasSize(2);
        assertThat(availableACP).extracting(AssociatedCollectionPoint::getName).containsOnly("Pickup One", "Pickup Three");
        assertThat(availableACP).extracting(AssociatedCollectionPoint::getCity).containsOnly("Aveiro", "Viseu");

        // Mockito verifications
        this.verifyStoreFindByIdIsCalled();
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGettingAvailableACP__withInvalidStoreID_thenExceptionThrown(){
        // Set up Expectations
        when(storeRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            storeService.getAvailableACP(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find Store with the ID -5!");

        // Mockito verifications
        this.verifyStoreFindByIdIsCalled();
    }



    // Auxilliary Functions

    private void verifyACPFindByIdIsCalled(){
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

    private void verifyStoreFindByIdIsCalled(){
        Mockito.verify(storeRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }
}
