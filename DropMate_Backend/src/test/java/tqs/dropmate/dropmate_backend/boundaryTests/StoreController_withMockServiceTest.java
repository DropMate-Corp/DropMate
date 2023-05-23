package tqs.dropmate.dropmate_backend.boundaryTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.dropmate.dropmate_backend.controllers.EstoreController;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.StoreService;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EstoreController.class)
public class StoreController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    private List<AssociatedCollectionPoint> availableACP;

    @BeforeEach
    public void setUp(){
        AssociatedCollectionPoint pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setName("Pickup One");
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");

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

        // Under Limit
        Map<String, Integer> statsMapThree = new HashMap<>();
        statsMapThree.put("total_parcels", 30);
        statsMapThree.put("parcels_in_delivery", 2);
        statsMapThree.put("parcels_waiting_pickup", 1);
        pickupPointThree.setOperationalStatistics(statsMapThree);

        availableACP = new ArrayList<>();

        availableACP.add(pickupPointOne);
        availableACP.add(pickupPointThree);
    }

    @AfterEach
    public void tearDown(){

    }

    @Test
    void whenCreatingOrder_withValidParameters_thenReturn_statusOK() throws Exception {
        // Setting up expectations
        Parcel testParcel = new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY);

        when(storeService.createNewOrder(1, 1)).thenReturn(testParcel);

        mockMvc.perform(
                        post("/dropmate/estore_api/parcel").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1")
                                .param("storeID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$.pickupCode", is("PCK123")))
                .andExpect(jsonPath("$.parcelStatus", is(Status.IN_DELIVERY.toString())));
    }

    @Test
    void whenCreatingOrder_withInvalidStoreID_thenReturn_statusNotFound() throws Exception {
        when(storeService.createNewOrder(1, -1)).thenThrow(new ResourceNotFoundException("Couldn't find Store with the ID -1!"));

        mockMvc.perform(
                        post("/dropmate/estore_api/parcel").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1")
                                .param("storeID", "-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreatingOrder_withInvalidACPID_thenReturn_statusNotFound() throws Exception {
        when(storeService.createNewOrder(-1, 1)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -1!"));

        mockMvc.perform(
                        post("/dropmate/estore_api/parcel").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "-1")
                                .param("storeID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGettingAvailableACP_withValidParameters_thenReturnOnlyACPSUnderLimit_statusOK() throws Exception {
        when(storeService.getAvailableACP(1)).thenReturn(availableACP);

        mockMvc.perform(
                        get("/dropmate/estore_api/acp").contentType(MediaType.APPLICATION_JSON)
                                .param("storeID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Pickup One")))
                .andExpect(jsonPath("$[1].name", is("Pickup Three")));
    }

    @Test
    void whenGettingAvailableACP__withInvalidStoreID_statusNotFound() throws Exception {
        when(storeService.getAvailableACP(-1)).thenThrow(new ResourceNotFoundException("Couldn't find Store with the ID -1!"));

        mockMvc.perform(
                        get("/dropmate/estore_api/acp").contentType(MediaType.APPLICATION_JSON)
                                .param("storeID", "-1"))
                .andExpect(status().isNotFound());
    }
}
