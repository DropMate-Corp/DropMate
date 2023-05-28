package tqs.dropmate.dropmate_backend.boundaryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.dropmate.dropmate_backend.controllers.AdminController;
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
class AdminController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private List<AssociatedCollectionPoint> allACP;
    private List<Parcel> parcelsWaitingDelivery;
    private List<Parcel> parcelsWaitingPickup;
    private AssociatedCollectionPoint pickupPointOne;
    private AssociatedCollectionPoint pickupPointTwo;
    private SystemAdministrator user;

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

        pickupPointTwo = new AssociatedCollectionPoint();
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

        // Parcels
        Parcel parcelDelOne = new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY);
        Parcel parcelDelTwo= new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY);

        parcelsWaitingDelivery = new ArrayList<>();
        parcelsWaitingDelivery.add(parcelDelOne);
        parcelsWaitingDelivery.add(parcelDelTwo);

        Parcel parcelPickOne = new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);
        Parcel parcelPickTwo = new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP);

        parcelsWaitingPickup = new ArrayList<>();
        parcelsWaitingPickup.add(parcelPickOne);
        parcelsWaitingPickup.add(parcelPickTwo);

        // System admin
        user = new SystemAdministrator();
        user.setName("User");
        user.setEmail("user@email.com");
        user.setPassword("password");
    }

    @AfterEach
    public void tearDown(){
        allACP = null;
        parcelsWaitingDelivery = null;
        parcelsWaitingPickup = null;
    }

    @Test
    void whenGetAllACP_thenReturn_statusOK() throws Exception {
        when(adminService.getAllACP()).thenReturn(allACP);

        mockMvc.perform(
                        get("/dropmate/admin/acp").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].city", is("Aveiro")))
                .andExpect(jsonPath("$[2].address", is("Fake address 3, Viseu")));
    }

    @Test
    void whenGetAllPendingACP_thenReturn_statusOK() throws Exception {
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);

        when(adminService.getAllPendingACP()).thenReturn(List.of(candidateACP));

        mockMvc.perform(
                        get("/dropmate/admin/acp/pending").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city", is("Aveiro")))
                .andExpect(jsonPath("$[0].address", is("Fake Street no 1, Aveiro")));
    }

    @Test
    void whenGetAllStores_thenReturn_statusOK() throws Exception {
        // Creating Fake Stores
        List<Store> allStores = new ArrayList<>();
        allStores.add(new Store("Store One", "one@mail.pt", "Aveiro", "Fake Adress 1, Aveiro", "000000000"));
        allStores.add(new Store("Store Two", "two@mail.pt", "Porto", "Fake Adress 2, Porto", "000000000"));


        when(adminService.getAllStores()).thenReturn(allStores);

        mockMvc.perform(
                        get("/dropmate/admin/estores").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].city", is("Aveiro")))
                .andExpect(jsonPath("$[1].address", is("Fake Adress 2, Porto")));
    }

    @Test
    void whenGetAllParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
        when(adminService.getAllParcelsWaitingDelivery()).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/all/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() throws Exception {
        when(adminService.getAllParcelsWaitingPickup()).thenReturn(parcelsWaitingPickup);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/all/pickup").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL790")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.WAITING_FOR_PICKUP.toString())))
                .andExpect(jsonPath("$[1].pickupCode", is("PCK803")));
    }

    @Test
    void whenGetParcelsWaitingDelivery_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
        when(adminService.getParcelsWaitingDeliveryAtACP(0)).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/0/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    void whenGetParcelsWaitingPickup_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
            when(adminService.getParcelsWaitingPickupAtACP(0)).thenReturn(parcelsWaitingPickup);

            mockMvc.perform(
                            get("/dropmate/admin/parcels/0/pickup").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].deliveryCode", is("DEL790")))
                    .andExpect(jsonPath("$[1].parcelStatus", is(Status.WAITING_FOR_PICKUP.toString())))
                    .andExpect(jsonPath("$[1].pickupCode", is("PCK803")));
    }

    @Test
    void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getParcelsWaitingDeliveryAtACP(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/parcels/-2/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getParcelsWaitingPickupAtACP(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/parcels/-2/pickup").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllACPOperationalStatistics_thenReturn_statusOK() throws Exception {
        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        allACP.forEach(acp -> {acp.setOperationalStatistics(statsMap); acp.setDeliveryLimit(10);});

        when(adminService.getAllACPStatistics()).thenReturn(allACP.stream()
                .collect(Collectors.toMap(
                        AssociatedCollectionPoint::getAcpId,
                        acp -> {
                            Map<String, Integer> statistics = new HashMap<>(acp.getOperationalStatistics());
                            statistics.put("deliveryLimit", acp.getDeliveryLimit());
                            return statistics;
                        }
                )));

        mockMvc.perform(
                        get("/dropmate/admin/acp/statistics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", everyItem(hasKey("total_parcels"))))
                .andExpect(jsonPath("$.*", everyItem(hasKey("parcels_waiting_pickup"))))
                .andExpect(jsonPath("$.*", everyItem(hasKey("deliveryLimit"))))
                .andExpect(jsonPath("$.*", everyItem(hasKey("parcels_in_delivery"))));
    }

    @Test
    void whenGetSpecificACPOperationalStatistics_withValidID_thenReturn_statusOK() throws Exception {
        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        allACP.forEach(acp -> {acp.setOperationalStatistics(statsMap); acp.setDeliveryLimit(10);});

        when(adminService.getSpecificACPStatistics(2)).thenReturn(allACP.get(1).getOperationalStatistics());

        mockMvc.perform(
                        get("/dropmate/admin/acp/2/statistics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_parcels", is(10)))
                .andExpect(jsonPath("$.parcels_waiting_pickup", is(3)))
                .andExpect(jsonPath("$.parcels_in_delivery", is(5)));
    }

    @Test
    void whenGetSpecificACPOperationalStatistics_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getSpecificACPStatistics(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/acp/-2/statistics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetSpecificACPDetails_withValidID_thenReturn_statusOK() throws Exception {
        when(adminService.getACPDetails(2)).thenReturn(allACP.get(1));

        mockMvc.perform(
                        get("/dropmate/admin/acp/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Porto")))
                .andExpect(jsonPath("$.address", is("Fake address 2, Porto")))
                .andExpect(jsonPath("$.deliveryLimit", is(15)));
    }

    @Test
    void whenGetSpecificACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getACPDetails(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/acp/-2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateACPDetails_withValidID_allFields_thenReturn_statusOK() throws Exception {
        // Preparing for the test
        pickupPointTwo.setName("test");
        pickupPointTwo.setEmail("newemail@mail.pt");
        pickupPointTwo.setAddress("Nevermore");
        pickupPointTwo.setCity("Lalaland");
        pickupPointTwo.setTelephoneNumber("000000000");

        // Setting up expectations
        when(adminService.updateACPDetails(2, "newemail@mail.pt", "test", "000000000", "Lalaland", "Nevermore"))
                .thenReturn(pickupPointTwo);


        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/2").contentType(MediaType.APPLICATION_JSON)
                                .param("name", "test")
                                .param("email", "newemail@mail.pt")
                                .param("telephone", "000000000")
                                .param("city", "Lalaland")
                                .param("address", "Nevermore"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.city", is("Lalaland")))
                .andExpect(jsonPath("$.address", is("Nevermore")))
                .andExpect(jsonPath("$.email", is("newemail@mail.pt")))
                .andExpect(jsonPath("$.deliveryLimit", is(15)));
    }

    @Test
    void whenUpdateACPDetails_withValidID_somFields_thenReturn_statusOK() throws Exception {
        // Preparing for the test
        pickupPointTwo.setAddress("Nevermore");
        pickupPointTwo.setCity("Lalaland");

        // Setting up expectations
        when(adminService.updateACPDetails(2, null, null, null, "Lalaland", "Nevermore"))
                .thenReturn(pickupPointTwo);

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/2").contentType(MediaType.APPLICATION_JSON)
                                .param("city", "Lalaland")
                                .param("address", "Nevermore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Lalaland")))
                .andExpect(jsonPath("$.address", is("Nevermore")))
                .andExpect(jsonPath("$.email", is("pickuptwo@mail.pt")))
                .andExpect(jsonPath("$.deliveryLimit", is(15)));
    }

    @Test
    void whenUpdateACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        // Setting up expectations
        when(adminService.updateACPDetails(-2, null, null, null, "Lalaland", "Nevermore"))
                .thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));;

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/-2").contentType(MediaType.APPLICATION_JSON)
                                .param("city", "Lalaland")
                                .param("address", "Nevermore"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAddNewPendingACP_thenReturn_correspondingACP() throws Exception {
        // Preparing for the test
        PendingACP candidateACP = new PendingACP();
        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);
        candidateACP.setAcpId(1);

        // Setting up expectations
        when(adminService.addNewPendingAcp("Test New ACP", "newacp@mail.pt", "Aveiro", "Fake Street no 1, Aveiro", "000000000", "I am a totally legit pickup point"))
                .thenReturn(candidateACP);

        // Performing the call
        mockMvc.perform(
                        post("/dropmate/admin/acp/pending").contentType(MediaType.APPLICATION_JSON)
                                .param("city", "Aveiro")
                                .param("address", "Fake Street no 1, Aveiro")
                                .param("name", "Test New ACP")
                                .param("email", "newacp@mail.pt")
                                .param("telephoneNumber", "000000000")
                                .param("description", "I am a totally legit pickup point"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Aveiro")))
                .andExpect(jsonPath("$.address", is("Fake Street no 1, Aveiro")))
                .andExpect(jsonPath("$.email", is("newacp@mail.pt")))
                .andExpect(jsonPath("$.acpId", is(1)))
                .andExpect(jsonPath("$.status", is(0)));
    }

    @Test
    void reviewCandidateACP_withValidID_notReviewedBefore_thenAcceptACP() throws Exception {
        // Set up Expectations
        when(adminService.changePendingACPStatus(1, 2)).thenReturn(new SuccessfulRequest("Request accepted!"));

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/pending/1/status").contentType(MediaType.APPLICATION_JSON)
                                .param("newStatus", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Request accepted!")));
    }

    @Test
    void reviewCandidateACP_withValidID_notReviewedBefore_rejectACP() throws Exception {
        // Set up Expectations
        when(adminService.changePendingACPStatus(1, 1)).thenReturn(new SuccessfulRequest("Request rejected!"));

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/pending/1/status").contentType(MediaType.APPLICATION_JSON)
                                .param("newStatus", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Request rejected!")));
    }

    @Test
    void reviewCandidateACP_withValidID_reviewedBefore_thenRejectNewEvaluation() throws Exception {
        // Set up Expectations
        when(adminService.changePendingACPStatus(1, 2)).thenReturn(new SuccessfulRequest("Operation denied, as this candidate request has already been reviewed!"));

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/pending/1/status").contentType(MediaType.APPLICATION_JSON)
                                .param("newStatus", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Operation denied, as this candidate request has already been reviewed!")));
    }

    @Test
    void reviewCandidateACP_withInvalidID_thenReceiveException() throws Exception {
        // Set up Expectations
        when(adminService.changePendingACPStatus(-1, 2)).thenThrow(new ResourceNotFoundException("Couldn't find candidate ACP with the ID -1!"));;

        // Performing the call
        mockMvc.perform(
                        put("/dropmate/admin/acp/pending/-1/status").contentType(MediaType.APPLICATION_JSON)
                                .param("newStatus", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteACP_withValidID_thenACPDeleted() throws Exception {
        // Set up Expectations
        when(adminService.removeACP(1)).thenReturn(new SuccessfulRequest("ACP succesfully deleted!"));

        // Performing the call
        mockMvc.perform(
                        delete("/dropmate/admin/acp/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("ACP succesfully deleted!")));
    }

    @Test
    void deleteACP_withInvalidID_thenThrowException() throws Exception {
        // Set up Expectations
        when(adminService.removeACP(-1)).thenThrow(new ResourceNotFoundException("Couldn't find candidate ACP with the ID -1!"));;

        // Performing the call
        mockMvc.perform(
                        delete("/dropmate/admin/acp/-1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenLoginValidUser_thenReturnUser_andStatus200() throws Exception {
        when(adminService.processAdminLogin(user.getEmail(), user.getPassword()))
                .thenReturn(user);

        mockMvc.perform(
                        post("/dropmate/admin/login")
                                .param("email", user.getEmail())
                                .param("password", user.getPassword()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));

        verify(adminService, times(1)).processAdminLogin(user.getEmail(), user.getPassword());
    }

    @Test
    void whenLoginWithInvalidPassword_thenReturnStatus401() throws Exception {
        String wrongPassword = "wrongPassword";
        when(adminService.processAdminLogin(user.getEmail(), wrongPassword))
                .thenThrow(new InvalidCredentialsException("Invalid login credentials"));

        mockMvc.perform(
                        post("/dropmate/admin/login")
                                .param("email", user.getEmail())
                                .param("password", wrongPassword).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login credentials"));

        verify(adminService, times(1)).processAdminLogin(user.getEmail(), wrongPassword);
    }

    @Test
    void whenLoginWithInvalidEmail_thenReturnStatus401() throws Exception {
        String wrongEmail = "wrongEmail@mail.com";

        when(adminService.processAdminLogin(wrongEmail, user.getPassword()))
                .thenThrow(new InvalidCredentialsException("Invalid login credentials"));

        mockMvc.perform(
                        post("/dropmate/admin/login")
                                .param("email", wrongEmail)
                                .param("password", user.getPassword()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login credentials"));

        verify(adminService, times(1)).processAdminLogin(wrongEmail, user.getPassword());
    }
}
