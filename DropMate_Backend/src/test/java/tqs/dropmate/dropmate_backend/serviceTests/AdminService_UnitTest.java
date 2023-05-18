package tqs.dropmate.dropmate_backend.serviceTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.services.AdminService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminService_UnitTest {
    @Mock(lenient = true)
    private AssociatedCollectionPointRepository acpRepository;

    @InjectMocks
    private AdminService adminService;

    // Expectations
    private List<AssociatedCollectionPoint> allACP;

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
    }

    @AfterEach
    public void tearDown(){
        allACP = null;
    }

    @Test
    public void whenGetAllAcp_thenReturnAllAcp(){
        // Set up Expectations
        when(adminService.getAllACP()).thenReturn(allACP);

        // Verify the result is as expected
        List<AssociatedCollectionPoint> returnedACP = adminService.getAllACP();
        assertThat(returnedACP).isEqualTo(allACP);
        assertThat(returnedACP).hasSize(3);
        assertThat(returnedACP).extracting(AssociatedCollectionPoint::getCity).contains("Aveiro", "Porto", "Viseu");

        // Verify that the external API was called and Verify that the cache was called twice - to query and to add the new record
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findAll();
    }
}
