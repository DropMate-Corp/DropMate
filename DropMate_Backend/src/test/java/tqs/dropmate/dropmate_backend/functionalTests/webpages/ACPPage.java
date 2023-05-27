package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ACPPage {
    private final WebDriver driver;
    private static final String URL = "http://localhost:5173/acp/";

    @FindBy(id = "acpName")
    WebElement acpName;

    public ACPPage(WebDriver driver, String acpId) {
        this.driver = driver;
        this.driver.get(URL + acpId);
        PageFactory.initElements(driver, this);
    }

    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean checkACPName(String name) {
        WebElement nameElement = waitForElement(By.id("acpName"));

        // Check if the name is displayed
        assert nameElement.isDisplayed();

        // Check if the name displayed is the same as the one passed as argument
        return nameElement.getText().equals(name);
    }

    public boolean checkACPStatistics(String limit, String inDelivery, String pickup, String total) {
        Logger logger = Logger.getLogger("TestLogger");

        WebElement limitElement = waitForElement(By.id("Parcel Limit"));
        WebElement inDeliveryElement = waitForElement(By.id("Parcels in Delivery"));
        WebElement pickupElement = waitForElement(By.id("Parcels Waiting Pickup"));
        WebElement totalElement = waitForElement(By.id("Total Parcels"));

        // Check if the statistics are displayed
        assert limitElement.isDisplayed();
        assert inDeliveryElement.isDisplayed();
        assert pickupElement.isDisplayed();
        assert totalElement.isDisplayed();

        // Check if the statistics displayed are the same as the ones passed as arguments and log the result
        boolean result = true;
        if (!limitElement.getText().equals(limit)) {
            logger.log(Level.SEVERE, "Limit: " + limitElement.getText() + " != " + limit);
            result = false;
        }
        if (!inDeliveryElement.getText().equals(inDelivery)) {
            logger.log(Level.SEVERE, "In Delivery: " + inDeliveryElement.getText() + " != " + inDelivery);
            result = false;
        }
        if (!pickupElement.getText().equals(pickup)) {
            logger.log(Level.SEVERE, "Pickup: " + pickupElement.getText() + " != " + pickup);
            result = false;
        }
        if (!totalElement.getText().equals(total)) {
            logger.log(Level.SEVERE, "Total: " + totalElement.getText() + " != " + total);
            result = false;
        }
        return result;
    }

}
