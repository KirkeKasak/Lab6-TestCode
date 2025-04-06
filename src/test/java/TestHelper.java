import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

public class TestHelper {

    static WebDriver driver;
    final int waitForResponseTime = 4;

	// here write a link to your admin website (e.g. http://my-app.herokuapp.com/admin)
    String baseUrlAdmin = "http://127.0.0.1:3000/admin";

	// here write a link to your website (e.g. http://my-app.herokuapp.com/)
    String baseUrl = "http://127.0.0.1:3000/";

    @Before
    public void setUp(){

        // if you use Chrome:
        //System.setProperty("webdriver.chrome.driver", "C:\\Users\\...\\chromedriver.exe");
        //driver = new ChromeDriver();

        // if you use Firefox:


        System.setProperty("webdriver.gecko.driver", "C:\\Users\\krike\\Downloads\\geckodriver-v0.36.0-win64\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(baseUrl);

    }

    void goToPage(String page){
        WebElement elem = driver.findElement(By.linkText(page));
        elem.click();
        waitForElementById(page);
    }

    WebElement waitForElementById(String id){
        return new WebDriverWait(driver, waitForResponseTime).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    WebElement waitForElement(By by){
        return new WebDriverWait(driver, waitForResponseTime).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    WebElement inputByValue(String value) {
        return driver.findElement(By.xpath("//input[@value='" + value + "']"));
    }

    WebElement inputByValueInElement(String value, WebElement element) {
        return element.findElement(By.xpath(".//input[@value='" + value + "']"));
    }

    void assertNotice(String noticeText) {
        WebElement notice = driver.findElement(By.id("notice"));
        assertEquals(noticeText, notice.getText());
    }

    public boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    void login(String username, String password){

        driver.get(baseUrlAdmin);

        driver.findElement(By.linkText("Login")).click();

        driver.findElement(By.id("name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.xpath("//input[@value='Login']")).click();
    }

    void logout(){
        WebElement logout = driver.findElement(By.linkText("Logout"));
        logout.click();

        waitForElementById("Admin");
    }

    void createAdminUser(String username, String password) {
        driver.get(baseUrlAdmin);
        goToPage("Register");

        driver.findElement(By.id("user_name")).sendKeys(username);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(password);

        inputByValue("Create User").click();
    }

    void removeAdminUser() {
        goToPage("Admin");
        if (isElementPresent(By.linkText("Delete"))) {
            driver.findElement(By.linkText("Delete")).click();
            assertNotice("User was successfully deleted.");
        }
    }



    void createProduct(String name, String category, String description, String price) {
        goToPage("Products");

        // Navigate to new product
        driver.findElement(By.linkText("New product")).click();

        String header = driver.findElement(By.className("product_header")).getText();
        assertEquals("New Product", header);

        // Fill the form and click Create
        driver.findElement(By.id("product_title")).sendKeys(name);
        driver.findElement(By.id("product_description")).sendKeys(description);

        Select dropdown = new Select(driver.findElement(By.id("product_prod_type")));
        dropdown.selectByVisibleText(category);

        driver.findElement(By.id("product_price")).sendKeys(price);

        inputByValue("Create Product").click();
    }

    void deleteProduct(String name) {
        goToPage("Products");

        // Wait for the new row and assert the contents
        WebElement productRow = waitForElementById(name);
        WebElement productDescriptionCell = productRow.findElement(By.className("list_description"));

        WebElement titleLink = productDescriptionCell.findElement(By.tagName("a"));
        assertEquals(name, titleLink.getText());

        // Delete the existing product and verify
        productRow.findElement(By.linkText("Delete")).click();
        assertNotice("Product was successfully destroyed.");
    }

    @After
    public void tearDown(){
        driver.close();
    }

}