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
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.ACPService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
}
