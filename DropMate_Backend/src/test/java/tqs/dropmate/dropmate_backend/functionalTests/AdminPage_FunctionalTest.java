package tqs.dropmate.dropmate_backend.functionalTests;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeAll;
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
    public void checkIfRegisteredACPsTableIsDisplayed(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        assertTrue(adminPage.checkIfRegisteredACPsTableIsDisplayed());
        driver.quit();
    }

    @Test
    @Disabled
    public void checkACPDetails(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        this.wait(1);
        adminPage.clickViewACPButton("1");

        ACPPage acpPage = new ACPPage(driver, "1");
        this.wait(1);
        assertTrue(acpPage.checkIfACPNameIsDisplayed());

        driver.quit();
    }

    public void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ignored) {
        }
    }
}
