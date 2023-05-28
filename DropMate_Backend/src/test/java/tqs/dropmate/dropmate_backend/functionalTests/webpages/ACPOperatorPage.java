package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ACPOperatorPage {
    private final WebDriver driver;
    private static final String URL = "http://localhost:5173/acp-operator/";

    @FindBy(id = "inDeliveryTable")
    WebElement inDeliveryTable;

    @FindBy(id = "waitingPickupTable")
    WebElement waitingPickupTable;

    @FindBy(id = "deliveredTable")
    WebElement deliveredTable;

    @FindBy(id = "editLimit")
    WebElement editLimitButton;

    @FindBy(id = "saveLimit")
    WebElement saveLimitButton;

    @FindBy(id = "limitInput")
    WebElement limitInput;

    public ACPOperatorPage(WebDriver driver) {
        this.driver = driver;
        this.driver.get(URL);
        PageFactory.initElements(driver, this);
    }

    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean checkListOfParcelsInDelivery() {
        // Check if the table is displayed
        assert inDeliveryTable.isDisplayed();

        // Check if the table has at least one row
        return inDeliveryTable.findElements(By.tagName("tr")).size() > 1;
    }

    public boolean checkListOfParcelsWaitingPickup() {
        // Check if the table is displayed
        assert waitingPickupTable.isDisplayed();

        // Check if the table has at least one row
        return waitingPickupTable.findElements(By.tagName("tr")).size() > 1;
    }

    public boolean checkListOfParcelsDelivered() {
        // Check if the table is displayed
        assert deliveredTable.isDisplayed();

        // Check if the table has at least one row
        return deliveredTable.findElements(By.tagName("tr")).size() > 1;
    }

    public boolean defineNewLimit(int newLimit) {
        // Wait for the edit button to be visible
        waitForElement(By.id("editLimit"));

        // Click on the edit button
        editLimitButton.click();

        // Wait for the input to be visible
        waitForElement(By.id("limitInput"));

        // Clear the input
        limitInput.clear();

        // Set the new limit
        limitInput.sendKeys(String.valueOf(newLimit));

        // Click on the save button
        saveLimitButton.click();

        // Wait for the input to be invisible
        waitForElement(By.id("limitInput"));

        // Check if the new limit is displayed
        return limitInput.getAttribute("value").equals(String.valueOf(newLimit));
    }
}
