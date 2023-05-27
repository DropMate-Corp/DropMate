package tqs.dropmate.dropmate_backend.functionalTests;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import tqs.dropmate.dropmate_backend.functionalTests.webpages.ACPPage;
import tqs.dropmate.dropmate_backend.functionalTests.webpages.AdminPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SeleniumJupiter.class)
public class AdminPage_FunctionalTest {
    @Test
    @Disabled
    public void testCheckIfRegisteredACPsTableIsDisplayed(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        assertTrue(adminPage.isRegisteredACPsTableDisplayed());
        driver.quit();
    }

    @Test
    @Disabled
    public void testCheckACPDetails(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        adminPage.clickViewACPButton("8");

        ACPPage acpPage = new ACPPage(driver, "8");
        assertTrue(acpPage.checkACPName("Jardim da Celeste"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testDeleteACP(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        adminPage.clickDeleteACPButton("8");
        adminPage.confirmDeleteACP();
        assertTrue(adminPage.isACPDeleted("8"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testCheckParcelsTable(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);

        // Check if parcels table is displayed
        assertTrue(adminPage.isParcelsTableDisplayed());

        // Change Delivery Status to IN_DELIVERY
        adminPage.selectDeliveryStatus("In Delivery");
        assertTrue(adminPage.checkIfAllParcelTableRowsAreWithTheRightStatus("IN_DELIVERY"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testCheckRegisteredPartnersTable(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);

        // Check if registered partners table is displayed
        assertTrue(adminPage.checkIfRegisteredPartnersTableIsDisplayed());

        driver.quit();
    }
}
