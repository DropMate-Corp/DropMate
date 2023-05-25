package tqs.dropmate.dropmate_backend.functionalTests;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import tqs.dropmate.dropmate_backend.functionalTests.webpages.AdminPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SeleniumJupiter.class)
public class AdminPage_FunctionalTest {
    @Test
    public void checkIfRegisteredACPsTableIsDisplayed(FirefoxDriver driver) {
        AdminPage adminPage = new AdminPage(driver);
        assertTrue(adminPage.checkIfRegisteredACPsTableIsDisplayed());
        driver.quit();
    }
}
