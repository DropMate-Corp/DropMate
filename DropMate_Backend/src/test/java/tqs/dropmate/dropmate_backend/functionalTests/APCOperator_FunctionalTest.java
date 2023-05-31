package tqs.dropmate.dropmate_backend.functionalTests;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import tqs.dropmate.dropmate_backend.functionalTests.webpages.ACPOperatorPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SeleniumJupiter.class)
public class APCOperator_FunctionalTest {

    @Test
    @Disabled
    public void testCheckListOfParcelsInDelivery(FirefoxDriver driver) {
        ACPOperatorPage acpOperatorPage = new ACPOperatorPage(driver);

        assertTrue(acpOperatorPage.checkListOfParcelsInDelivery());
    }

    @Test
    @Disabled
    public void testCheckListOfParcelsWaitingPickup(FirefoxDriver driver) {
        ACPOperatorPage acpOperatorPage = new ACPOperatorPage(driver);

        assertTrue(acpOperatorPage.checkListOfParcelsWaitingPickup());
    }

    @Test
    @Disabled
    public void testCheckListOfParcelsDelivered(FirefoxDriver driver) {
        ACPOperatorPage acpOperatorPage = new ACPOperatorPage(driver);

        assertTrue(acpOperatorPage.checkListOfParcelsDelivered());
    }

    @Test
    @Disabled
    public void testEditLimit(FirefoxDriver driver) {
        ACPOperatorPage acpOperatorPage = new ACPOperatorPage(driver);

        assertTrue(acpOperatorPage.defineNewLimit(15));
    }
}
