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
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.services.StoreService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EstoreController.class)
class StoreController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;
    @MockBean
    private AdminService adminService;

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
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("status", Status.IN_DELIVERY.toString());
        returnMap.put("delivery_date", Date.valueOf(LocalDate.now().plusDays(5)).toString());
        returnMap.put("pickup_code", "PCK123");

        when(storeService.createNewOrder(1, 1)).thenReturn(returnMap);

        mockMvc.perform(
                        post("/dropmate/estore_api/parcel").contentType(MediaType.APPLICATION_JSON)
                                .param("acpID", "1")
                                .param("storeID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickup_code", is("PCK123")))
                .andExpect(jsonPath("$.status", is(Status.IN_DELIVERY.toString())))
                .andExpect(jsonPath("$.delivery_date", is(Date.valueOf(LocalDate.now().plusDays(5)).toString())));
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
    void whenGettingAvailableACP_withInvalidStoreID_statusNotFound() throws Exception {
        when(storeService.getAvailableACP(-1)).thenThrow(new ResourceNotFoundException("Couldn't find Store with the ID -1!"));

        mockMvc.perform(
                        get("/dropmate/estore_api/acp").contentType(MediaType.APPLICATION_JSON)
                                .param("storeID", "-1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void whenGetParcelStatus_withValidPickupCode_thenReturnStatusOK() throws Exception {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("status", Status.DELIVERED.toString());
        returnMap.put("delivery_date", Date.valueOf(LocalDate.now().plusDays(5)).toString());
        returnMap.put("pickup_date", Date.valueOf(LocalDate.now().plusDays(15)).toString());

        when(storeService.getParcelStatus("PCKD3674")).thenReturn(returnMap);

        mockMvc.perform(
                        get("/dropmate/estore_api/parcel/PCKD3674").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.DELIVERED.toString())))
                .andExpect(jsonPath("$.delivery_date", is(Date.valueOf(LocalDate.now().plusDays(5)).toString())))
                .andExpect(jsonPath("$.pickup_date", is(Date.valueOf(LocalDate.now().plusDays(15)).toString())));
    }

    @Test
    void whenGetParcelStatus_withInvalidPickupCode_statusNotFound() throws Exception {
        when(storeService.getParcelStatus("PCKD3674")).thenThrow(new ResourceNotFoundException("Couldn't find Parcel with the pickup Code PCKD3674!"));

        mockMvc.perform(
                        get("/dropmate/estore_api/parcel/PCKD3674").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetSpecificACPDetails_withValidID_thenReturn_statusOK() throws Exception {
        when(adminService.getACPDetails(2)).thenReturn(availableACP.get(1));

        mockMvc.perform(
                        get("/dropmate/estore_api/acp/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Viseu")))
                .andExpect(jsonPath("$.address", is("Fake address 3, Viseu")))
                .andExpect(jsonPath("$.deliveryLimit", is(12)));
    }

    @Test
    void whenGetSpecificACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        when(adminService.getACPDetails(-2)).thenThrow(new ResourceNotFoundException("Couldn't find ACP with the ID -2!"));

        mockMvc.perform(
                        get("/dropmate/estore_api/acp/-2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
