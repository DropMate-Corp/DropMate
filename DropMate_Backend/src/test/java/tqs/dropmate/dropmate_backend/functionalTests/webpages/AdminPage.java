package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminPage {
    private final WebDriver driver;
    private static final String URL = "http://localhost:5173/admin";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    @FindBy(id = "registeredACPs")
    WebElement registeredACPsContainer;

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.driver.get(URL);
        PageFactory.initElements(driver, this);
    }

    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isRegisteredACPsTableDisplayed() {
        WebElement table = waitForElement(By.tagName("table"));
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
        WebElement table = waitForElement(By.tagName("table"));
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
}
