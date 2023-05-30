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
public class Admin_FunctionalTest {
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
        adminPage.clickViewACPButton("1");

        ACPPage acpPage = new ACPPage(driver, "1");
        assertTrue(acpPage.checkACPName("Pracinha das Flores"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testCheckACPStatistics(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        adminPage.clickViewACPButton("1");

        ACPPage acpPage = new ACPPage(driver, "1");
        assertTrue(acpPage.checkACPStatistics("10", "1", "1", "3"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testDeleteACP(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        adminPage.clickDeleteACPButton("1");
        adminPage.confirmDeleteACP();
        assertTrue(adminPage.isACPDeleted("1"));

        driver.quit();
    }

    @Test
    @Disabled
    public void testCheckParcelsTableInDelivery(FirefoxDriver driver) {
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
    public void testCheckParcelsTableWaitingPickup(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);

        // Check if parcels table is displayed
        assertTrue(adminPage.isParcelsTableDisplayed());

        // Change Delivery Status to WAITING_PICKUP
        adminPage.selectDeliveryStatus("Waiting For Pickup");
        assertTrue(adminPage.checkIfAllParcelTableRowsAreWithTheRightStatus("WAITING_FOR_PICKUP"));

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

    @Test
    @Disabled
    public void testCheckStatisticsTable(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);

        // Check if ACP statistics table is displayed
        assertTrue(adminPage.checkIfACPStatisticsTableIsDisplayed());

        driver.quit();
    }

    @Test
    @Disabled
    public void testReviewACPRequest(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);

        // Check if ACP statistics table is displayed
        assertTrue(adminPage.reviewACPRequest("2"));

        driver.quit();
    }
}
