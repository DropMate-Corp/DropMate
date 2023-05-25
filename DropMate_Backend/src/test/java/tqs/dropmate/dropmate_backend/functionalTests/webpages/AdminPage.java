package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AdminPage {
    private final WebDriver driver;
    private static final String URL = "http://localhost:5173/admin";

    @FindBy(id = "registeredACPs")
    WebElement registeredACPsContainer;

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.driver.get(URL);
        PageFactory.initElements(driver, this);
    }

    public Boolean checkIfRegisteredACPsTableIsDisplayed() {
        // Check if parent container has a child with the tag "table"
        WebElement table = registeredACPsContainer.findElement(By.tagName("table"));

        // Check if table is displayed
        return table.isDisplayed();
    }

    public void clickViewACPButton(String acpId) {
        String buttonId = "viewACP" + acpId;
        WebElement viewButton = this.driver.findElement(By.id(buttonId));
        viewButton.click();
    }
}
