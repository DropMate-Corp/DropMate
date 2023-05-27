package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
}
