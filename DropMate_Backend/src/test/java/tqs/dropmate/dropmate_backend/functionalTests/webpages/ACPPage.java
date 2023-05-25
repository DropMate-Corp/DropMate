package tqs.dropmate.dropmate_backend.functionalTests.webpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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

    public Boolean checkIfACPNameIsDisplayed() {
        return acpName.isDisplayed();
    }
}
