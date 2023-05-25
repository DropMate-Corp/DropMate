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
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.services.ACPService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ACPService_UnitTest {
    @Mock(lenient = true)
    private AssociatedCollectionPointRepository acpRepository;

    @InjectMocks
    private ACPService acpService;

    // Expectations
    private AssociatedCollectionPoint pickupPointOne;

    @BeforeEach
    public void setUp(){
        pickupPointOne = new AssociatedCollectionPoint();
        pickupPointOne.setName("Pickup One");
        pickupPointOne.setCity("Aveiro");
        pickupPointOne.setAddress("Fake address 1, Aveiro");
        pickupPointOne.setEmail("pickupone@mail.pt");
        pickupPointOne.setDeliveryLimit(10);
        pickupPointOne.setTelephoneNumber("953339994");
    }

    @AfterEach
    public void tearDown(){

    }

    @Test
    void whenGetACPDelivery_withValidID_thenReturnLimit() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        assertThat(acpService.getDeliveryLimit(1)).isEqualTo(10);

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenGetACPDelivery_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.getDeliveryLimit(-5);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenUpdateACPDelivery_withValidID_thenReturnLimit() throws ResourceNotFoundException {
        // Set up Expectations
        when(acpRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(pickupPointOne));

        // Verify the result is as expected
        assertThat(acpService.updateDeliveryLimit(1, 50)).isEqualTo(50);

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    @Test
    void whenUpdateACPDelivery_withInvalidID_thenThrowException(){
        // Set up Expectations
        when(acpRepository.findById(-5)).thenReturn(Optional.empty());

        // Verify the result is as expected
        assertThatThrownBy(() -> {
            acpService.updateDeliveryLimit(-5, 50);
        }).isInstanceOf(ResourceNotFoundException.class).hasMessageContainingAll("Couldn't find ACP with the ID -5!");

        // Mockito verifications
        this.verifyACPFindByIdIsCalled();
    }

    // Auxilliary Functions

    private void verifyACPFindByIdIsCalled(){
        Mockito.verify(acpRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }

}
