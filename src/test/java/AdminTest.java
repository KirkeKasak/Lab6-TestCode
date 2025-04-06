import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertEquals;

public class AdminTest extends TestHelper {


    private String username = "Coccinella2";
    private String password = "T2piline666";


    @Test
    public void registerAccountTest() {
        createAdminUser(username, password);
        WebElement notice = driver.findElement(By.id("notice"));
        assertEquals("User " + username + " was successfully created.", notice.getText());
    }


    @Test
    public void registerAccount_NonMatchingPassword_Test() {
        driver.get(baseUrlAdmin);
        goToPage("Register");

        driver.findElement(By.id("user_name")).sendKeys(username);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(password + "fail");

        inputByValue("Create User").click();

        WebElement notice = driver.findElement(By.id("error_explanation"));
        assertEquals("1 error prohibited this user from being saved:\n" +
                "Password confirmation doesn't match Password", notice.getText());
    }

    @Test
    public void deleteAccountTest() {
        createAdminUser(username, password);
        goToPage("Admin");
        // Explicitly delete user, will fail if button is not there
        driver.findElement(By.linkText("Delete")).click();
        assertNotice("User was successfully deleted.");
    }

    @Test
    public void addProductTest() {
        createAdminUser(username, password);
        String name = "The River";
        String category = "Books";
        createProduct(name, category, "The River (Väylä) is a 2021 novel by author Rosa Liksom.", "39.99");
        deleteProduct(name, category);
    }

    @Test
    public void addProduct_InvalidData_Test() {
        createAdminUser(username, password);

        driver.findElement(By.linkText("New product")).click();

        String header = driver.findElement(By.className("product_header")).getText();
        assertEquals("New Product", header);

        driver.findElement(By.id("product_title")).sendKeys("Random");
        driver.findElement(By.id("product_price")).sendKeys("invalid");

        inputByValue("Create Product").click();

        WebElement notice = driver.findElement(By.id("error_explanation"));
        assertEquals("2 errors prohibited this product from being saved:\n"
                + "Description can't be blank\n" +
                "Price is not a number", notice.getText());

    }


    @Test
    public void loginLogoutTest() {
        createAdminUser(username, password);
        logout();
        login(username, password);

        waitForElementById("Products");
        WebElement adminHeader = driver.findElement(By.xpath("//a[@href=\"/admin\"]"));
        assertEquals("Admin", adminHeader.getText());

    }

    @Test
    public void login_InvalidPassword_test() {
        createAdminUser(username, password);
        logout();
        login(username, "invalidPassword");
        assertNotice("Invalid user/password combination");
        login(username, password);
    }


    @After
    public void cleanupAdmin() {
        removeAdminUser();
    }
}
