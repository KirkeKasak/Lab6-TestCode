import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class EndUserTest extends TestHelper {

    String product1Name = "B45593 Sunglasses";
    String product2Name = "Web Application Testing Book";
    String product3Name = "Sunglasses 2AR";

    @Test
    public void addProductToCartTest() {
        addProductToCart(product1Name, 3);
        assertProductInCart(product1Name, 3, 1);
    }

    @Test
    public void addToCar_MultipleProducts_Test() {
        addProductToCart(product1Name, 3);
        addProductToCart(product2Name, 4);
        assertProductInCart(product1Name, 3, 1);
        assertProductInCart(product2Name, 4, 2);
    }

    @Test
    public void addProduct_andEmptyCart_Test() {
        addProductToCart(product1Name);

        inputByValue("Empty cart").click();
        assertNotice("Cart successfully deleted.");
    }

    @Test
    public void filterByCategory_andAddToCart_Test() {
        driver.findElement(By.linkText("Books")).click();
        addProductToCart(product2Name, 2);
        assertProductInCart(product2Name, 2, 1);
    }

    @Test
    public void searchByName_andAddToCart_Test() {
        driver.findElement(By.id("search_input")).sendKeys("ar");
        addProductToCart(product3Name);
        assertProductInCart(product3Name, 1, 1);
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

    void assertProductInCart(String productName, int quantity, int row) {
        WebElement cart = driver.findElement(By.id("cart"));

        WebElement targetRow = cart.findElement(By.xpath("(//tr[contains(@class, 'cart_row')])[" + (row) + "]"));

        // Quantity (first <td>)
        String actualQuantityText = targetRow.findElement(By.xpath("td[1]")).getText().trim();
        assertEquals("Wrong quantity at row " + row, quantity + "×", actualQuantityText);

        // Product name (second <td>)
        String actualProductName = targetRow.findElement(By.xpath("td[2]")).getText().trim();
        assertEquals("Wrong product at row " + row, productName, actualProductName);
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
