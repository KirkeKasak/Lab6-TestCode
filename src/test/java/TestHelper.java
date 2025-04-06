import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

public class TestHelper {

    static WebDriver driver;
    final int waitForResposeTime = 4;

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

    void waitForElementById(String id){
        new WebDriverWait(driver, waitForResposeTime).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    WebElement inputByValue(String value) {
        return driver.findElement(By.xpath("//input[@value='" + value + "']"));
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

    @After
    public void tearDown(){
        driver.close();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

}