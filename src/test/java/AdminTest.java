import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class AdminTest extends TestHelper {


    private String username = "Coccinella2";
    private String password = "T2piline666";


    @Test
    public void registerAccount_Success_Test() {
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
    public void loginLogoutTest(){
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
