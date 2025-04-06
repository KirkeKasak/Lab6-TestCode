import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class AdminTest extends TestHelper {


    private String username = "Coccinella2";
    private String password = "T2piline666";


    @Test
    public void registerAccountSuccessTest() {
        driver.get(baseUrlAdmin);
        goToPage("Register");

        driver.findElement(By.id("user_name")).sendKeys(username);
        driver.findElement(By.id("user_password")).sendKeys(password);
        driver.findElement(By.id("user_password_confirmation")).sendKeys(password);

        inputByValue("Create User").click();

        WebElement notice = driver.findElement(By.id("notice"));
        assertEquals("User Coccinella2 was successfully created.", notice.getText());

    }

    @Test
    public void registerAccountNonMatchingPasswordTest() {
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

    @After
    public void deleteUser() {
        goToPage("Admin");
        if (isElementPresent(By.linkText("Delete"))) {
            driver.findElement(By.linkText("Delete")).click();
            assertNotice("User was successfully deleted.");
        }
    }
}
