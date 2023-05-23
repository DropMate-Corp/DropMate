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
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.StoreService;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EstoreController.class)
public class StoreController_withMockServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @BeforeEach
    public void setUp(){

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
}
