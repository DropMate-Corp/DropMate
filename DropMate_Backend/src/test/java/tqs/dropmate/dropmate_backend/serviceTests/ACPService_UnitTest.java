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
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.services.ACPService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ACPService_UnitTest {
    @Mock(lenient = true)
    private AssociatedCollectionPointRepository acpRepository;
    @Mock(lenient = true)
    private ParcelRepository parcelRepository;

    @InjectMocks
    private ACPService acpService;

    // Expectations
    private AssociatedCollectionPoint pickupPointOne;
    private List<Parcel> allParcels;

    @BeforeEach
    public void setUp(){
        pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setName("Pickup One");
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");

        // Creating Fake Parcels
        Parcel parcelDelOne = new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY);
        Parcel parcelDelTwo= new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY);
        Parcel parcelPickOne = new Parcel("DEL790", "PCK356", 1.5, new java.sql.Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);
        Parcel parcelPickTwo = new Parcel("DEL367", "PCK803", 2.2, new java.sql.Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);
        Parcel parcelOne = new Parcel("DEL000", "PCK257", 1.5, new java.sql.Date(2023, 5, 22), new java.sql.Date(2023, 5, 28), Status.DELIVERED);
        Parcel parcelTwo = new Parcel("DEL843", "PCK497", 1.6, new java.sql.Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED);

        parcelDelOne.setPickupACP(pickupPointOne);
        parcelPickOne.setPickupACP(pickupPointOne);
        parcelOne.setPickupACP(pickupPointOne);
        parcelDelTwo.setPickupACP(pickupPointOne);
        parcelPickTwo.setPickupACP(pickupPointOne);
        parcelTwo.setPickupACP(pickupPointOne);

        allParcels = new ArrayList<>();
        allParcels.add(parcelDelOne);
        allParcels.add(parcelDelTwo);
        allParcels.add(parcelPickOne);
        allParcels.add(parcelPickTwo);
        allParcels.add(parcelOne);
        allParcels.add(parcelTwo);
    }

    @AfterEach
    public void tearDown(){
        allParcels = null;
    }

    @Test
    void whenGetACPDelivery_withValidID_thenReturnLimit() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        assertThat(acpService.getDeliveryLimit(1)).isEqualTo(10);

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGetACPDelivery_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.getDeliveryLimit(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenUpdateACPDelivery_withValidID_thenReturnLimit() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        assertThat(acpService.updateDeliveryLimit(1, 50)).isEqualTo(50);

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenUpdateACPDelivery_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.updateDeliveryLimit(-5, 50);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsWaitingDelivery_withValidID_thenReturnCorrectParcels() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));
        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = acpService.getAllParcelsWaitingDelivery(1);
        assertThat(returnedParcels).hasSize(2);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.IN_DELIVERY);
        assertThat(returnedParcels).extracting(Parcel::getDeliveryCode).containsOnly("DEL123", "DEL456");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsWaitingDelivery_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.getAllParcelsWaitingDelivery(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsWaitingPickup_withValidID_thenReturnCorrectParcels() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));
        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = acpService.getAllParcelsWaitingForPickup(1);
        assertThat(returnedParcels).hasSize(2);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.WAITING_FOR_PICKUP);
        assertThat(returnedParcels).extracting(Parcel::getPickupCode).containsOnly("PCK356", "PCK803");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsWaitingPickup_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.getAllParcelsWaitingForPickup(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsDelivered_withValidID_thenReturnCorrectParcels() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));
        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = acpService.getAllParcelsDelivered(1);
        assertThat(returnedParcels).hasSize(2);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.DELIVERED);
        assertThat(returnedParcels).extracting(Parcel::getDeliveryCode).containsOnly("DEL000", "DEL843");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGettingParcelsDelivered_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.getAllParcelsDelivered(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenDoingCheckIn_existingParcel_validDeliveryCode_thenReturnParcel() throws InvalidCredentialsException, ResourceNotFoundException {
        // Set up Expectations
        when(parcelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(allParcels.get(0)));

        // Verify the result is as expected
        Parcel updatedParcel = acpService.checkInProcess(1, "DEL123");
        assertThat(updatedParcel.getDeliveryCode()).isEqualTo("DEL123");
        assertThat(updatedParcel.getParcelStatus()).isEqualTo(Status.WAITING_FOR_PICKUP);
        assertThat(updatedParcel.getDeliveryDate()).isEqualTo(Date.valueOf(LocalDate.now()));

        // Mockito verifications
        this.verifyParcelFindByIdIsCalled();
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).save(updatedParcel);
    }

    @Test
    void whenDoingCheckIn_existingParcel_invalidDeliveryCode_thenThrowException(){
        // Set up Expectations
        when(parcelRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(allParcels.get(0)));

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.checkInProcess(1, "WRONGCODE");
        }).isInstanceOf(InvalidCredentialsException.class).hasMessageContainingAll("Request denied. Delivery code inputted by Operator doesn't match the code of the parcel.");

        // Mockito verifications
        this.verifyParcelFindByIdIsCalled();
    }

    @Test
    void whenDoingCheckIn_nonExistingParcel_thenThrowException(){

        // Set up Expectations
        when(parcelRepository.findById(-1)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.checkInProcess(-1, "DEL123");
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -1!");

        // Mockito verifications
        this.verifyParcelFindByIdIsCalled();
    }

    // Auxilliary Functions
    private void verifyACPFindByIdIsCalled(){
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

    private void verifyParcelFindByIdIsCalled(){
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

    private void verifyFindAllParcels(){
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findAll();
    }
}
