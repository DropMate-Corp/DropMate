package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminPage {
    private final WebDriver driver;
    private static final String URL = "http://localhost:5173/admin";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.driver.get(URL);
    }

    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isRegisteredACPsTableDisplayed() {
        WebElement table = waitForElement(By.id("registeredACPsTable"));
        return table.isDisplayed();
    }

    public void clickViewACPButton(String acpId) {
        String buttonId = "viewACP" + acpId;
        WebElement viewButton = waitForElement(By.id(buttonId));
        viewButton.click();
    }

    public void clickDeleteACPButton(String acpId) {
        String buttonId = "deleteACP" + acpId;
        WebElement deleteButton = waitForElement(By.id(buttonId));
        deleteButton.click();
    }

    public void confirmDeleteACP() {
        WebElement confirmButton = waitForElement(By.id("confirmDeleteACP"));
        confirmButton.click();
    }

    public boolean isACPDeleted(String acpId) {
        WebElement table = waitForElement(By.id("registeredACPsTable"));
        assert table.isDisplayed();

        String rowId = "row" + acpId;
        boolean rowFound = true;
        try {
            table.findElement(By.id(rowId));
        } catch (NoSuchElementException e) {
            rowFound = false;
        }
        return !rowFound;
    }

    public void selectDeliveryStatus(String status) {
        WebElement selectElement = waitForElement(By.name("deliveryStatus"));
        selectElement.sendKeys(status);
    }

    public boolean checkIfAllParcelTableRowsAreWithTheRightStatus(String status) {
        WebElement table = waitForElement(By.id("parcelsTable"));
        assert table.isDisplayed();

        List<WebElement> rows = table.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.size() > 0) {
                String deliveryStatus = columns.get(6).getText();
                if (!deliveryStatus.equals(status)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isParcelsTableDisplayed() {
        WebElement table = waitForElement(By.id("parcelsTable"));
        return table.isDisplayed();
    }

    public boolean checkIfRegisteredPartnersTableIsDisplayed() {
        WebElement table = waitForElement(By.id("registeredPartnersTable"));
        return table.isDisplayed();
    }

    public boolean checkIfACPStatisticsTableIsDisplayed() {
        WebElement table = waitForElement(By.id("ACPStatisticsTable"));
        return table.isDisplayed();
    }
}
