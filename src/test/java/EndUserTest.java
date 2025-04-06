import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class EndUserTest extends TestHelper {

    String product1Name = "B45593 Sunglasses";
    String product2Name = "Sunglasses 2AR";
    String product3Name = "Sunglasses B45593";
    String product4Name = "Web Application Testing Book";


    @Test
    public void addProductToCartTest() {
        addProductToCart(product1Name, 3);

        assertProductInCart(product1Name, 3, 1);
        assertCartRowCount(1);
        assertTotal("€78.00");
    }

    @Test
    public void addProduct_andEmptyCart_Test() {
        addProductToCart(product1Name);

        inputByValue("Empty cart").click();
        assertNotice("Cart successfully deleted.");
    }

    @Test
    public void addMultipleToCart_Test() {
        addProductToCart(product1Name, 3);
        addProductToCart(product4Name, 4);

        assertProductInCart(product1Name, 3, 1);
        assertProductInCart(product4Name, 4, 2);

        assertCartRowCount(2);
        assertTotal("€197.96");
    }

    @Test
    public void addToCart_andChangeQuantity() {
        addProductToCart(product4Name, 3);

        increaseQuantityOfCartRow(1);
        assertProductInCart(product4Name, 4, 1);

        decreaseQuantityOfCartRow(1);
        assertProductInCart(product4Name, 3, 1);

        assertCartRowCount(1);
        assertTotal("€89.97");
    }

    @Test
    public void addMultipleToCart_andRemoveOne() {
        addProductToCart(product4Name, 9);
        addProductToCart(product2Name, 1);

        assertCartRowCount(2);
        assertTotal("€295.91");

        deleteProductOnCartRow(1);

        assertCartRowCount(1);
        assertProductInCart(product2Name, 1, 1);
        assertTotal("€26.00");
    }


    @Test
    public void addMultipleToCart_AndChangeQuantities_Test() {
        addProductToCart(product1Name, 2);
        addProductToCart(product2Name, 5);
        addProductToCart(product4Name, 3);

        increaseQuantityOfCartRow(3);
        decreaseQuantityOfCartRow(2);
        deleteProductOnCartRow(1);
        increaseQuantityOfCartRow(2);

        assertProductInCart(product4Name, 4, 1);
        assertProductInCart(product2Name, 5, 2);

        assertCartRowCount(2);
        assertTotal("€249.96");
    }

    @Test
    public void filterByCategory_andAddToCart_Test() {
        driver.findElement(By.linkText("Books")).click();
        addProductToCart(product4Name, 2);
        assertProductInCart(product4Name, 2, 1);
    }

    @Test
    // Negative test for missing elements
    public void filterByCategory_andTrAddToCart_Test() {
        driver.findElement(By.linkText("Sunglasses")).click();
        assertFalse("Element should appear in search", isElementPresent(By.id(product4Name + "_entry")));
    }

    @Test
    public void searchByName_andAddToCart_Test() {
        driver.findElement(By.id("search_input")).sendKeys("ar");
        addProductToCart(product2Name);
        assertProductInCart(product2Name, 1, 1);
    }

    @Test
    public void checkoutSuccessfullyTest() {
        // add products and checkout
        addProductToCart(product1Name, 1);
        addProductToCart(product3Name, 3);
        addProductToCart(product4Name, 4);

        inputByValue("Checkout").click();

        // Fill order form and place order
        waitForElementById("order_page");

        driver.findElement(By.id("order_name")).sendKeys("New order");
        driver.findElement(By.id("order_address")).sendKeys("Narva mnt 18, 51009 Tartu");
        driver.findElement(By.id("order_email")).sendKeys("test@test.com");

        Select dropdown = new Select(driver.findElement(By.id("order_pay_type")));
        dropdown.selectByVisibleText("Credit card");

        inputByValue("Place Order").click();

        // Verify order form content
        waitForElementById("order_receipt");

        String name = driver.findElement(By.xpath("//p[strong[text()='Name:']]")).getText();
        assertEquals("Name: New order", name);

        String address = driver.findElement(By.xpath("//p[strong[text()='Address:']]")).getText();
        assertEquals("Address: Narva mnt 18, 51009 Tartu", address);

        String email = driver.findElement(By.xpath("//p[strong[text()='Email:']]")).getText();
        assertEquals("Email: test@test.com", email);

        String payType = driver.findElement(By.xpath("//p[strong[text()='Pay type:']]")).getText();
        assertEquals("Pay type: Credit card", payType);

        // Verify products added
        assertCheckoutRow(1, 1, "B45593 Sunglasses", "€26.00");
        assertCheckoutRow(2, 3, "Sunglasses B45593", "€78.00");
        assertCheckoutRow(3, 4, "Web Application Testing Book", "€119.96");

        assertCheckoutTotal("€739.84");
    }

    @Test
    // Negative test, invalid input data
    public void checkout_withValidationErrorsTest() {
        addProductToCart(product1Name, 1);
        addProductToCart(product3Name, 3);
        addProductToCart(product4Name, 4);

        inputByValue("Checkout").click();
        waitForElementById("order_page");

        inputByValue("Place Order").click();

        WebElement error = driver.findElement(By.id("error_explanation"));

        // First row is commented out in HTML!, Should it be?
        assertEquals(// "4 errors prohibited this user from being saved:\n" +
                "Name can't be blank\n" +
                "Address can't be blank\n" +
                "Email can't be blank\n" +
                "Pay type is not included in the list", error.getText());
    }


    @Test
    // Negative test for missing elements
    public void searchByName_andTrySelectingMissingItem_Test() {
        driver.findElement(By.id("search_input")).sendKeys("sung");

        addProductToCart(product1Name);
        assertTrue("Element should appear in search", driver.findElement(By.id(product2Name + "_entry")).isDisplayed());
        assertFalse("Element should not appear in search", driver.findElement(By.id(product4Name + "_entry")).isDisplayed());

        assertProductInCart(product1Name, 1, 1);
    }


    void addProductToCart(String name) {
        addProductToCart(name, 1);
    }

    void addProductToCart(String name, long quantity) {
        WebElement productRow = waitForElementById(name + "_entry");
        WebElement addToCart = inputByValueInElement("Add to Cart", productRow);

        for (int i = 0; i < quantity; i++) {
            addToCart.click();
        }
    }

    void assertTotal(String expected) {
        String total = driver.findElement(By.className("total_cell")).getText();
        assertEquals(expected, total);
    }

    void assertCartRowCount(int expectedRowCount) {
        WebElement cart = driver.findElement(By.id("cart"));
        int actualRowCount = cart.findElements(By.cssSelector("tr.cart_row")).size();
        assertEquals("Cart row count mismatch", expectedRowCount, actualRowCount);
    }


    void assertProductInCart(String productName, int quantity, int row) {
        WebElement targetRow = getCartRowByIndex(row);

        // Quantity (first <td>)
        String actualQuantityText = targetRow.findElement(By.xpath("td[1]")).getText().trim();
        assertEquals("Wrong quantity at row " + row, quantity + "×", actualQuantityText);

        // Product name (second <td>)
        String actualProductName = targetRow.findElement(By.xpath("td[2]")).getText().trim();
        assertEquals("Wrong product at row " + row, productName, actualProductName);
    }

    void increaseQuantityOfCartRow(int row) {
        WebElement targetRow = getCartRowByIndex(row);
        WebElement increaseButton = targetRow.findElement(By.xpath("td[@class='quantity'][2]/a"));
        increaseButton.click();
    }

    void decreaseQuantityOfCartRow(int row) {
        WebElement targetRow = getCartRowByIndex(row);
        WebElement decreaseButton = targetRow.findElement(By.xpath("td[@class='quantity'][1]/a"));
        decreaseButton.click();
    }

    void deleteProductOnCartRow(int row) {
        WebElement targetRow = getCartRowByIndex(row);
        WebElement deleteButton = targetRow.findElement(By.cssSelector("td#delete_button a"));
        deleteButton.click();
    }

    private WebElement getCartRowByIndex(int row) {
        WebElement cart = driver.findElement(By.id("cart"));
        return cart.findElement(By.xpath("(//tr[contains(@class, 'cart_row')])[" + row + "]"));
    }

    void assertCheckoutRow(int rowIndex, int quantity, String productName, String price) {
        WebElement row = driver.findElement(By.xpath("(//table[@id='check_out']//tr)[" + rowIndex + "]"));

        String actualQuantity = row.findElement(By.xpath("td[1]")).getText().trim();
        String actualProductName = row.findElement(By.xpath("td[2]")).getText().trim();
        String actualPrice = row.findElement(By.xpath("td[3]")).getText().trim();

        assertEquals(quantity + "×", actualQuantity);
        assertEquals(productName, actualProductName);
        assertEquals(price, actualPrice);
    }

    void assertCheckoutTotal(String expectedTotal) {
        WebElement totalCell = driver.findElement(By.cssSelector("#check_out tr.total_line td.total_cell strong"));
        assertEquals(expectedTotal, totalCell.getText().trim());
    }

    @After
    public void tearDown() {
        goToPage("Home");
        By xpath = By.xpath("//input[@value='Empty cart']");
        if (isElementPresent(xpath)) {
            driver.findElement(xpath).click();
            assertNotice("Cart successfully deleted.");
        }
    }

}
