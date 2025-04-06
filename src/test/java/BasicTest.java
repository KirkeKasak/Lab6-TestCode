import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class BasicTest extends TestHelper {


    private String username = "Coccinella";
    private String password = "T2piline66";

    @Test
    public void titleExistsTest(){
        String expectedTitle = "ST Online Store";
        String actualTitle = driver.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }


    @Test
    public void loginLogoutTest(){

        login(username, password);

        waitForElementById("Products");
        WebElement adminHeader = driver.findElement(By.xpath("//a[@href=\"/admin\"]"));
        assertEquals("Admin", adminHeader.getText());
        logout();
    }

    @Test
    public void loginFalsePassword() {
        login(username, "invalidPassword");

        WebElement notice = driver.findElement(By.id("notice"));
        assertEquals("Invalid user/password combination", notice.getText());
    }

}
