import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertEquals;

public class AdminTest extends TestHelper {


    private String username = "Coccinella2";
    private String password = "T2piline666";

    // Default product info
    String productName = "The River";
    String productCategory = "Books";
    String productDescription = "The River (Väylä) is a 2021 novel by author Rosa Liksom.";
    String productPrice = "39.90";

    @Test
    public void registerAccountTest() {
        createAdminUser(username, password);
        WebElement notice = driver.findElement(By.id("notice"));
        assertEquals("User " + username + " was successfully created.", notice.getText());
    }


    @Test
    // Negative test invalid password confirm
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
    public void addAndDeleteProductTest() {
        createAdminUser(username, password);
        createProduct(productName, productCategory, productDescription, productPrice);
        WebElement categorySpan = driver.findElement(By.className("prod_categ"));
        assertEquals(productCategory, categorySpan.getText());
        deleteProduct(productName);
    }

    @Test
    // Negative test invalid input data
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
    // Expected to fail on v2.1!
    public void editProductTest() {
        createAdminUser(username, password);
        createProduct(productName, productCategory, productDescription, productPrice);

        WebElement productRow = waitForElementById(productName);
        productRow.findElement(By.linkText("Edit")).click();

        String header = driver.findElement(By.className("product_header")).getText();
        assertEquals("Editing Product", header);

        // Fill the form and click Update
        WebElement titleField = driver.findElement(By.id("product_title"));
        titleField.clear();
        titleField.sendKeys("Something Else");

        WebElement descField = driver.findElement(By.id("product_description"));
        descField.clear();
        descField.sendKeys("Entirely");

        Select dropdown = new Select(driver.findElement(By.id("product_prod_type")));
        dropdown.selectByVisibleText("Other");

        WebElement priceField = driver.findElement(By.id("product_price"));
        priceField.clear();
        priceField.sendKeys("5.0");

        inputByValue("Update Product").click();

        try {
            // Assert changes
            assertNotice("Product was successfully updated.");

            String title = driver.findElement(By.xpath("//p[strong[text()='Title:']]")).getText();
            assertEquals("Title: Something Else", title);

            String description = driver.findElement(By.xpath("//p[strong[text()='Description:']]")).getText();
            assertEquals("Description: Entirely", description);

            String price = driver.findElement(By.xpath("//p[strong[text()='Price:']]")).getText();
            assertEquals("Price: €5.00", price);

            String type = driver.findElement(By.xpath("//p[strong[text()='Type:']]")).getText();
            // This fails due to bug Number 1 in code
            assertEquals("Type: Other", type);


        } finally {
            // Ensure product is always deleted even when assertion fails due to bugs
            deleteProduct("Something Else");
        }
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
    // Negative test
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
