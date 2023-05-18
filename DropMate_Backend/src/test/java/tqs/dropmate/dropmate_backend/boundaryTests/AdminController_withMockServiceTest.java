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
import tqs.dropmate.dropmate_backend.services.ACPService;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.services.StoreService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
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


    @BeforeEach
    public void setUp(){
        // Creating test pickup points
        AssociatedCollectionPoint pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");

        AssociatedCollectionPoint pickupPointTwo = new AssociatedCollectionPoint();
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

        parcelsWaitingDelivery.add(parcelDelOne);
        parcelsWaitingDelivery.add(parcelDelTwo);
    }

    @AfterEach
    public void tearDown(){
        allACP = null;
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
    public void whenGetAllAParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
        when(adminService.getAllParcelsWaitingDelivery()).thenReturn(parcelsWaitingDelivery);

        mockMvc.perform(
                        get("/dropmate/admin/parcels/all/delivery").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].deliveryCode", is("DEL123")))
                .andExpect(jsonPath("$[1].parcelStatus", is(Status.IN_DELIVERY)));
    }
}
