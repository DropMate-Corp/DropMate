package tqs.dropmate.dropmate_backend.serviceTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.*;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.sql.Date;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminService_UnitTest {
    @Mock(lenient = true)
    private AssociatedCollectionPointRepository acpRepository;
    @Mock(lenient = true)
    private ParcelRepository parcelRepository;
    @Mock(lenient = true)
    private StoreRepository storeRepository;
    @Mock(lenient = true)
    private SystemAdministratorRepository systemAdministratorRepository;
    @Mock(lenient = true)
    private PendingAssociatedCollectionPointRepository pendingACPRepository;

    @InjectMocks
    private AdminService adminService;

    // Expectations
    private List<AssociatedCollectionPoint> allACP;
    private List<Parcel> allParcels;
    private AssociatedCollectionPoint pickupPointOne;

    @BeforeEach
    public void setUp(){
        // Creating test pickup points
        pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");
        pickupPointOne.setAcpId(1);

        AssociatedCollectionPoint pickupPointTwo = new AssociatedCollectionPoint();
        pickupPointTwo.setCity("Porto");
        pickupPointTwo.setAddress("Fake address 2, Porto");
        pickupPointTwo.setEmail("pickuptwo@mail.pt");
        pickupPointTwo.setDeliveryLimit(15);
        pickupPointTwo.setTelephoneNumber("939333594");
        pickupPointTwo.setAcpId(2);

        AssociatedCollectionPoint pickupPointThree = new AssociatedCollectionPoint();
        pickupPointThree.setCity("Viseu");
        pickupPointThree.setAddress("Fake address 3, Viseu");
        pickupPointThree.setEmail("pickupthree@mail.pt");
        pickupPointThree.setDeliveryLimit(12);
        pickupPointThree.setTelephoneNumber("900000000");
        pickupPointThree.setAcpId(3);

        // Adding to ACP list
        allACP = new ArrayList<>();
        allACP.add(pickupPointOne);
        allACP.add(pickupPointTwo);
        allACP.add(pickupPointThree);

        // Creating Fake Parcels
        Parcel parcelDelOne = new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY);
        Parcel parcelDelTwo= new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY);
        Parcel parcelPickOne = new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);
        Parcel parcelPickTwo = new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);
        Parcel parcelOne = new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED);
        Parcel parcelTwo = new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED);

        parcelDelOne.setPickupACP(pickupPointOne);
        parcelPickOne.setPickupACP(pickupPointOne);
        parcelOne.setPickupACP(pickupPointOne);
        parcelDelTwo.setPickupACP(pickupPointTwo);
        parcelPickTwo.setPickupACP(pickupPointTwo);
        parcelTwo.setPickupACP(pickupPointTwo);

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
        allACP = null;
        allParcels = null;
    }

    @Test
    void whenGetAllAcp_thenReturnAllAcp(){
        // Set up Expectations
        when(acpRepository.findAll()).thenReturn(allACP);

        // Verify the result is as expected
        List<AssociatedCollectionPoint> returnedACP = adminService.getAllACP();
        assertThat(returnedACP)
                .isEqualTo(allACP)
                .hasSize(3)
                .extracting(AssociatedCollectionPoint::getCity).contains("Aveiro", "Porto", "Viseu");

        // Mockito verifications
        this.verifyFindAllIsCalled();
    }

    @Test
    void whenGetAllPendingAcp_thenReturnAllPendingAcp(){
        // Set up Expectations
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);


        when(pendingACPRepository.findAll()).thenReturn(List.of(candidateACP));

        // Verify the result is as expected
        List<PendingACP> returnedACP = adminService.getAllPendingACP();
        assertThat(returnedACP)
                .hasSize(1)
                .extracting(PendingACP::getCity).contains("Aveiro");

        // Mockito verifications
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllStores_thenReturnAllStores(){
        // Creating Fake Stores
        List<Store> allStores = new ArrayList<>();
        allStores.add(new Store("Store One", "one@mail.pt", "Aveiro", "Fake Adress 1, Aveiro", "000000000"));
        allStores.add(new Store("Store Two", "two@mail.pt", "Porto", "Fake Adress 2, Porto", "000000000"));


        // Set up Expectations
        when(storeRepository.findAll()).thenReturn(allStores);

        // Verify the result is as expected
        List<Store> returnedStores = adminService.getAllStores();
        assertThat(returnedStores)
                .isEqualTo(allStores)
                .hasSize(2)
                .extracting(Store::getCity).contains("Aveiro", "Porto");

        // Mockito verifications
        Mockito.verify(storeRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllParcelWaitDelivery_thenReturnOnlyDelivery(){
        // Set up Expectations
        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = adminService.getAllParcelsWaitingDelivery();
        assertThat(returnedParcels).hasSize(2);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.IN_DELIVERY);

        // Mockito verifications
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllParcelWaitPickup_thenReturnOnlyPickup(){
        // Set up Expectations
        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = adminService.getAllParcelsWaitingPickup();
        assertThat(returnedParcels).hasSize(2);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.WAITING_FOR_PICKUP);

        // Mockito verifications
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllParcelWaitDelivery_atSpecificACP_withValidACP_thenReturnOnlyCorrectACP() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));

        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = adminService.getParcelsWaitingDeliveryAtACP(1);
        assertThat(returnedParcels).hasSize(1);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.IN_DELIVERY);

        // Mockito verifications
        this.verifyFindByIdIsCalled();
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllParcelWaitPickup_atSpecificACP_withValidACP_thenReturnOnlyCorrectACP() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(allACP.get(1)));

        when(parcelRepository.findAll()).thenReturn(allParcels);

        // Verify the result is as expected
        List<Parcel> returnedParcels = adminService.getParcelsWaitingPickupAtACP(1);
        assertThat(returnedParcels).hasSize(1);
        assertThat(returnedParcels).extracting(Parcel::getParcelStatus).containsOnly(Status.WAITING_FOR_PICKUP);

        // Mockito verifications
        this.verifyFindByIdIsCalled();
        Mockito.verify(parcelRepository, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    void whenGetAllParcelWaitDelivery_atSpecificACP_withInvalidACP_thenThrowException() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            adminService.getParcelsWaitingDeliveryAtACP(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");


        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void whenGetAllParcelWaitPickup_atSpecificACP_withValidACP_thenThrowException() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            adminService.getParcelsWaitingPickupAtACP(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void whenGetAllOperationalStatistics_thenReturnAll(){
        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        allACP.forEach(acp -> {acp.setOperationalStatistics(statsMap); acp.setDeliveryLimit(10);});

        // Set up Expectations
        when(acpRepository.findAll()).thenReturn(allACP);

        // Verify the result is as expected
        Map<Integer, Map<String, Integer>> stats = adminService.getAllACPStatistics();
        assertThat(stats).hasSize(3);
        assertThat(stats.keySet()).contains(1,2,3);

        assertThat(stats).containsKey(1)
                .satisfies(map -> {
                    Assertions.assertThat(map.get(1)).containsEntry("parcels_in_delivery", 5);
                    Assertions.assertThat(map.get(1)).containsEntry("deliveryLimit", 10);
                });

        // Mockito verifications
        this.verifyFindAllIsCalled();
    }

    @Test
    void whenGetSpecificOperationalStatisics_withValidACP_thenReturnOnlySpecificACP() throws ResourceNotFoundException {
        // Set up Expectations
        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        pickupPointOne.setOperationalStatistics(statsMap);

        when(acpRepository.findById(5)).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        Map<String, Integer> returnedStats = adminService.getSpecificACPStatistics(5);

        assertThat(returnedStats)
                .hasSize(4)
                .containsKeys("total_parcels", "parcels_in_delivery", "parcels_waiting_pickup", "deliveryLimit")
                .containsEntry("deliveryLimit", 10);

        /// Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void whenGetSpecificOperationalStatisics_withInvalidACP_thenReturnException() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            adminService.getSpecificACPStatistics(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void getACPDetails_withValidACP_thenReturnDetails() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(5)).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        AssociatedCollectionPoint acp = adminService.getACPDetails(5);

        assertThat(acp).isEqualTo(pickupPointOne);

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void getACPDetails_withInvalidACP_thenReturnException() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            adminService.getACPDetails(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void updateACPDetails_allFieldsPassed() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(5)).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        AssociatedCollectionPoint acp = adminService.updateACPDetails(5, "newemail@mail.pt", "test", "000000000", "Lalaland", "Nevermore");

        assertThat(acp).extracting(AssociatedCollectionPoint::getCity).isEqualTo("Lalaland");
        assertThat(acp).extracting(AssociatedCollectionPoint::getEmail).isEqualTo("newemail@mail.pt");
        assertThat(acp).extracting(AssociatedCollectionPoint::getName).isEqualTo("test");
        assertThat(acp).extracting(AssociatedCollectionPoint::getTelephoneNumber).isEqualTo("000000000");
        assertThat(acp).extracting(AssociatedCollectionPoint::getAddress).isEqualTo("Nevermore");
        assertThat(acp).extracting(AssociatedCollectionPoint::getDeliveryLimit).isEqualTo(10);

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void updateACPDetails_someFieldsPassed() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(5)).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        AssociatedCollectionPoint acp = adminService.updateACPDetails(5, null, null, null, "Lalaland", "Nevermore");

        assertThat(acp).extracting(AssociatedCollectionPoint::getCity).isEqualTo("Lalaland");
        assertThat(acp).extracting(AssociatedCollectionPoint::getEmail).isEqualTo(pickupPointOne.getEmail());
        assertThat(acp).extracting(AssociatedCollectionPoint::getName).isEqualTo(pickupPointOne.getName());
        assertThat(acp).extracting(AssociatedCollectionPoint::getTelephoneNumber).isEqualTo(pickupPointOne.getTelephoneNumber());
        assertThat(acp).extracting(AssociatedCollectionPoint::getAddress).isEqualTo("Nevermore");
        assertThat(acp).extracting(AssociatedCollectionPoint::getDeliveryLimit).isEqualTo(10);

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void updateACPDetails_invalidID() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            adminService.updateACPDetails(-5, null, null, null, "Lalaland", "Nevermore");
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyFindByIdIsCalled();
    }

    @Test
    void addNewCandidateACP_thenReturnNewPendingACP() {
        // Preparing the test
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);

        // Set up Expectations
        when(pendingACPRepository.findFirstByName("Test New ACP")).thenReturn(candidateACP);

        // Verify the result is as expected
        assertThat(adminService.addNewPendingAcp("Test New ACP", "newacp@mail.pt", "Aveiro", "Fake Street no 1, Aveiro", "000000000", "I am a totally legit pickup point"))
                .isEqualTo(candidateACP);

        // Mockito verifications
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).findFirstByName(Mockito.any());
    }

    @Test
    void reviewCandidateACP_notReviewedBefore_acceptACP() throws ResourceNotFoundException {
        // Preparing the test
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);

        // Set up Expectations
        when(pendingACPRepository.findById(1)).thenReturn(Optional.of(candidateACP));

        // Verify the result is as expected
        assertThat(adminService.changePendingACPStatus(1, 2))
                .extracting(SuccessfulRequest::getMessage).isEqualTo("Request accepted!");

        // Mockito verifications
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).save(Mockito.any());
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).save(Mockito.any());
    }

    @Test
    void reviewCandidateACP_notReviewedBefore_rejectACP() throws ResourceNotFoundException {
        // Preparing the test
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);

        // Set up Expectations
        when(pendingACPRepository.findById(1)).thenReturn(Optional.of(candidateACP));

        // Verify the result is as expected
        assertThat(adminService.changePendingACPStatus(1, 1))
                .extracting(SuccessfulRequest::getMessage).isEqualTo("Request rejected!");

        // Mockito verifications
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).save(Mockito.any());
    }

    @Test
    void reviewCandidateACP_reviewedBefore_thenRejectNewEvaluation() throws ResourceNotFoundException {
        // Preparing the test
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(1);
        candidateACP.setAcpId(1);

        // Set up Expectations
        when(pendingACPRepository.findById(1)).thenReturn(Optional.of(candidateACP));

        // Verify the result is as expected
        assertThat(adminService.changePendingACPStatus(1, 1))
                .extracting(SuccessfulRequest::getMessage).isEqualTo("Operation denied, as this candidate request has already been reviewed!");

        // Mockito verifications
        Mockito.verify(pendingACPRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

    @Test
    void removeExistingACP() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(1)).thenReturn(Optional.of(pickupPointOne));

        // Verify the result is as expected
        assertThat(adminService.removeACP(1))
                .extracting(SuccessfulRequest::getMessage).isEqualTo("ACP succesfully deleted!");

        // Mockito verifications
        this.verifyFindByIdIsCalled();
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).delete(Mockito.any());
    }

    @Test
    void whenLoginValidUser_thenReturnUser () throws InvalidCredentialsException {
        // Set Up Expectations
        SystemAdministrator admin = new SystemAdministrator();
        admin.setName("User");
        admin.setEmail("user@email.com");
        admin.setPassword("password");

        when(systemAdministratorRepository.findByEmail(admin.getEmail())).thenReturn(admin);
        SystemAdministrator loggedUser = adminService.processAdminLogin(admin.getEmail(), admin.getPassword());

        // Verify the result is as expected
        assertThat(loggedUser).isNotNull();
        assertThat(loggedUser.getName()).isEqualTo(admin.getName());
        assertThat(loggedUser.getEmail()).isEqualTo(admin.getEmail());
        assertThat(loggedUser.getPassword()).isEqualTo(admin.getPassword());

        // Mockito verifications
        verify(systemAdministratorRepository, times(1)).findByEmail(admin.getEmail());
    }

    @Test
    void whenLoginWithInvalidEmail_thenThrowInvalidCredentialsException () {
        // Set Up Expectations
        SystemAdministrator admin = new SystemAdministrator();
        admin.setName("User");
        admin.setEmail("user@email.com");
        admin.setPassword("password");

        when(systemAdministratorRepository.findByEmail(admin.getEmail())).thenReturn(null);

        // Verify the result is as expected
        assertThatThrownBy(() -> adminService.processAdminLogin(admin.getEmail(), admin.getPassword()))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid login credentials");

        // Mockito verifications
        verify(systemAdministratorRepository, times(1)).findByEmail(admin.getEmail());
    }

    @Test
    void whenLoginWithInvalidPassword_thenThrowInvalidCredentialsException () {
        // Set Up Expectations
        SystemAdministrator admin = new SystemAdministrator();
        admin.setName("User");
        admin.setEmail("user@email.com");
        admin.setPassword("password");

        when(systemAdministratorRepository.findByEmail(admin.getEmail())).thenReturn(admin);

        // Verify the result is as expected
        assertThatThrownBy(() -> adminService.processAdminLogin(admin.getEmail(), "invalidPassword"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid login credentials");

        // Mockito verifications
        verify(systemAdministratorRepository, times(1)).findByEmail(admin.getEmail());
    }

    // Auxilliary functions
    private void verifyFindByIdIsCalled(){
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

    private void verifyFindAllIsCalled(){
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findAll();
    }
}
