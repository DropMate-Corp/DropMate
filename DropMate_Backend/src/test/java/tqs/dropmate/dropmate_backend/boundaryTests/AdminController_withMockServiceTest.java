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
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.AdminService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
public class AdminController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private List<AssociatedCollectionPoint> allACP;
    private List<Parcel> parcelsWaitingDelivery;
    private List<Parcel> parcelsWaitingPickup;
    private AssociatedCollectionPoint pickupPointOne;
    private AssociatedCollectionPoint pickupPointTwo;

    @BeforeEach
    public void setUp(){
        // Creating test pickup points
        pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");

        pickupPointTwo = new AssociatedCollectionPoint();
        pickupPointTwo.setCity("Porto");
        pickupPointTwo.setAddress("Fake address 2, Porto");
        pickupPointTwo.setEmail("pickuptwo@mail.pt");
        pickupPointTwo.setDeliveryLimit(15);
        pickupPointTwo.setTelephoneNumber("939333594");

        AssociatedCollectionPoint pickupPointThree = new AssociatedCollectionPoint();
        pickupPointThree.setCity("Viseu");
        pickupPointThree.setAddress("Fake address 3, Viseu");
        pickupPointThree.setEmail("pickupthree@mail.pt");
        pickupPointThree.setDeliveryLimit(12);
        pickupPointThree.setTelephoneNumber("900000000");

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
    }

    @AfterEach
    public void tearDown(){
        allACP = null;
        parcelsWaitingDelivery = null;
        parcelsWaitingPickup = null;
    }

    @Test
    public void whenGetAllACP_thenReturn_statusOK() throws Exception {
        when(adminService.getAllACP()).thenReturn(allACP);

        mockMvc.perform(
                        get("/dropmate/admin/acp").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].city", is("Aveiro")))
                .andExpect(jsonPath("$[2].address", is("Fake address 3, Viseu")));
    }

    @Test
    public void whenGetAllParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
        when(adminService.getAllParcelsWaitingDelivery()).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/all/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    public void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() throws Exception {
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
    public void whenGetParcelsWaitingDelivery_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
        when(adminService.getParcelsWaitingDeliveryAtACP(0)).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/0/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    public void whenGetParcelsWaitingPickup_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
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
    public void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getParcelsWaitingDeliveryAtACP(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/parcels/-2/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getParcelsWaitingPickupAtACP(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/parcels/-2/pickup").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetAllACPOperationalStatistics_thenReturn_statusOK() throws Exception {
        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        allACP.forEach(acp -> {acp.setOperationalStatistics(statsMap); acp.setDeliveryLimit(10);});

        when(adminService.getAllACPStatistics()).thenReturn(allACP.stream()
                .collect(Collectors.toMap(
                        acp -> acp,
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
    public void whenGetSpecificACPOperationalStatistics_withValidID_thenReturn_statusOK() throws Exception {
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
    public void whenGetSpecificACPOperationalStatistics_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getSpecificACPStatistics(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/acp/-2/statistics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetSpecificACPDetails_withValidID_thenReturn_statusOK() throws Exception {
        when(adminService.getACPDetails(2)).thenReturn(allACP.get(1));

        mockMvc.perform(
                        get("/dropmate/admin/acp/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Porto")))
                .andExpect(jsonPath("$.address", is("Fake address 2, Porto")))
                .andExpect(jsonPath("$.deliveryLimit", is(15)));
    }

    @Test
    public void whenGetSpecificACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getACPDetails(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/admin/acp/-2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
