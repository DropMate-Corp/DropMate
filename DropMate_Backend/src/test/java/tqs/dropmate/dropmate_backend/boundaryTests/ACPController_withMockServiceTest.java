package tqs.dropmate.dropmate_backend.boundaryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.dropmate.dropmate_backend.controllers.ACPController;
import tqs.dropmate.dropmate_backend.datamodel.ACPOperator;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.ACPService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ACPController.class)
class ACPController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ACPService acpService;

    private List<Parcel> parcelsWaitingDelivery;
    private List<Parcel> parcelsWaitingPickup;
    private List<Parcel> parcelsDelivered;
    private ACPOperator user;

    @BeforeEach
    public void setUp(){
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

        Parcel parcelDoneOne = new Parcel("DEL123", "PCK864", 1.5, new Date(2023, 5, 22), null, Status.DELIVERED);
        Parcel parcelDoneTwo = new Parcel("DEL643", "PCK267", 2.2, new Date(2023, 5, 22), null, Status.DELIVERED);

        parcelsDelivered = new ArrayList<>();
        parcelsDelivered.add(parcelDoneOne);
        parcelsDelivered.add(parcelDoneTwo);

        // ACP Operator
        user = new ACPOperator();
        user.setName("User");
        user.setEmail("user@email.com");
        user.setPassword("password");
    }

    @AfterEach
    public void tearDown(){
        parcelsWaitingDelivery = null;
        parcelsWaitingPickup = null;
        parcelsDelivered = null;
    }

    @Test
    void whenGetACPDelivery_withValidID_thenReturn_StatusOK() throws Exception {
       when(acpService.getDeliveryLimit(1)).thenReturn(10);

        mockMvc.perform(
                        get("/dropmate/acp_api/limit").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(10)));
    }


    @Test
    void whenGetACPDelivery_withInvalidID_thenReturn_StatusNotFound() throws Exception {
        when(acpService.getDeliveryLimit(-1)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        get("/dropmate/acp_api/limit").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateACPDelivery_withValidID_thenReturn_StatusOK() throws Exception {
        when(acpService.updateDeliveryLimit(1, 50)).thenReturn(50);

        mockMvc.perform(
                        put("/dropmate/acp_api/limit").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1")
                                .param("deliveryLimit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(50)));
    }

    @Test
    void whenUpdateACPDelivery_withInvalidID_thenReturn_StatusNotFound() throws Exception {
        when(acpService.updateDeliveryLimit(-1, 50)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        put("/dropmate/acp_api/limit").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-1")
                                .param("deliveryLimit", "50"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
        when(acpService.getAllParcelsWaitingDelivery(1)).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/delivery").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() throws Exception {
        when(acpService.getAllParcelsWaitingForPickup(1)).thenReturn(parcelsWaitingPickup);

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/pickup").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL790")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.WAITING_FOR_PICKUP.toString())))
                .andExpect(jsonPath("$[1].pickupCode", is("PCK803")));
    }

    @Test
    void whenGetAllAParcelsDelivered_thenReturn_statusOK() throws Exception {
        when(acpService.getAllParcelsDelivered(1)).thenReturn(parcelsDelivered);

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/delivered").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.DELIVERED.toString())))
                .andExpect(jsonPath("$[1].pickupCode", is("PCK267")));
    }

    @Test
    void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(acpService.getAllParcelsWaitingDelivery(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/delivery").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(acpService.getAllParcelsWaitingForPickup(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/pickup").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetParcelsDelivered_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(acpService.getAllParcelsDelivered(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/all/delivered").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDoingCheckIn_existingParcel_validDeliveryCode_thenReturn_statusOK() throws Exception {
        Parcel testParcel = parcelsWaitingDelivery.get(0);
        testParcel.setParcelStatus(Status.WAITING_FOR_PICKUP);
        testParcel.setDeliveryDate(Date.valueOf(LocalDate.now()));

        when(acpService.checkInProcess(1, "DEL123")).thenReturn(testParcel);

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/1/checkin").contentType(MediaType.APPLICATION_JSON)
                                .param("deliveryCode", "DEL123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$.parcelStatus", is(Status.WAITING_FOR_PICKUP.toString())))
                .andExpect(jsonPath("$.deliveryDate", is(Date.valueOf(LocalDate.now()).toString())));
    }

    @Test
    void whenDoingCheckIn_existingParcel_invalidDeliveryCode_thenReturn_statusNotFound() throws Exception {
        when(acpService.checkInProcess(1, "WRONGCODE")).thenThrow(new InvalidCredentialsException("Request denied. Delivery code inputted by Operator doesn't match the code of the parcel."));

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/1/checkin").contentType(MediaType.APPLICATION_JSON)
                                .param("deliveryCode", "WRONGCODE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenDoingCheckIn_nonExistingParcel_thenReturn_statusNotFound() throws Exception {
        when(acpService.checkInProcess(-1, "DEL123")).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/-1/checkin").contentType(MediaType.APPLICATION_JSON)
                                .param("deliveryCode", "DEL123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDoingCheckOut_existingParcel_validPickupCode_thenReturn_statusOK() throws Exception {
        Parcel testParcel = parcelsWaitingPickup.get(0);
        testParcel.setParcelStatus(Status.DELIVERED);
        testParcel.setPickupDate(Date.valueOf(LocalDate.now()));

        when(acpService.checkOutProcess(1, "PCK356")).thenReturn(testParcel);

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/1/checkout").contentType(MediaType.APPLICATION_JSON)
                                .param("pickupCode", "PCK356"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupCode", is("PCK356")))
                .andExpect(jsonPath("$.parcelStatus", is(Status.DELIVERED.toString())))
                .andExpect(jsonPath("$.pickupDate", is(Date.valueOf(LocalDate.now()).toString())));
    }

    @Test
    void whenDoingCheckOut_existingParcel_invalidPickupCode_thenReturn_statusNotFound() throws Exception {
        when(acpService.checkOutProcess(1, "WRONGCODE")).thenThrow(new InvalidCredentialsException("Request denied. Pickup code inputted by Operator doesn't match the code of the parcel."));

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/1/checkout").contentType(MediaType.APPLICATION_JSON)
                                .param("pickupCode", "WRONGCODE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenDoingCheckOut_nonExistingParcel_thenReturn_statusNotFound() throws Exception {
        when(acpService.checkOutProcess(-1, "PCK356")).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        put("/dropmate/acp_api/parcel/-1/checkout").contentType(MediaType.APPLICATION_JSON)
                                .param("pickupCode", "PCK356"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetParcelInfo_withValidID_thenReturn_statusOK() throws Exception {
        Parcel testParcel = parcelsDelivered.get(0);

        when(acpService.getParcelInfo(1)).thenReturn(testParcel);

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$.parcelStatus", is(Status.DELIVERED.toString())));
    }

    @Test
    void whenGetParcelInfo_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(acpService.getParcelInfo(-1)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        get("/dropmate/acp_api/parcel/-1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenLoginValidUser_thenReturnUser_andStatus200() throws Exception {
        when(acpService.processOperatorLogin(user.getEmail(), user.getPassword()))
                .thenReturn(user);

        mockMvc.perform(
                        post("/dropmate/acp_api/login")
                                .param("email", user.getEmail())
                                .param("password", user.getPassword()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));

        verify(acpService, times(1)).processOperatorLogin(user.getEmail(), user.getPassword());
    }

    @Test
    void whenLoginWithInvalidPassword_thenReturnStatus401() throws Exception {
        String wrongPassword = "wrongPassword";
        when(acpService.processOperatorLogin(user.getEmail(), wrongPassword))
                .thenThrow(new InvalidCredentialsException("Invalid login credentials"));

        mockMvc.perform(
                        post("/dropmate/acp_api/login")
                                .param("email", user.getEmail())
                                .param("password", wrongPassword).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login credentials"));

        verify(acpService, times(1)).processOperatorLogin(user.getEmail(), wrongPassword);
    }

    @Test
    void whenLoginWithInvalidEmail_thenReturnStatus401() throws Exception {
        String wrongEmail = "wrongEmail@mail.com";

        when(acpService.processOperatorLogin(wrongEmail, user.getPassword()))
                .thenThrow(new InvalidCredentialsException("Invalid login credentials"));

        mockMvc.perform(
                        post("/dropmate/acp_api/login")
                                .param("email", wrongEmail)
                                .param("password", user.getPassword()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid login credentials"));

        verify(acpService, times(1)).processOperatorLogin(wrongEmail, user.getPassword());
    }
}
