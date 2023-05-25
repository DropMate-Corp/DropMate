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
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.ACPService;

import static org.hamcrest.CoreMatchers.is;
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

    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){

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
}
