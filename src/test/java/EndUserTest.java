import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class EndUserTest extends TestHelper {

    String product1Name = "B45593 Sunglasses";

    @Test
    public void addProductsToCartTest() {
        WebElement productRow = waitForElementById(product1Name + "_entry");
        WebElement addToCart = inputByValueInElement("Add to Cart", productRow);
        addToCart.click();
        addToCart.click();
        addToCart.click();

        assertProductInCart(product1Name, 3, 1);
        inputByValue("Empty cart").click();
    }

    void assertProductInCart(String productName, int expectedQuantity, int row) {
        WebElement cart = driver.findElement(By.id("cart"));

        WebElement targetRow = cart.findElement(By.xpath("(//tr[contains(@class, 'cart_row')])[" + (row) + "]"));

        // Quantity (first <td>) - compare string like "3×"
        String expectedQuantityText = expectedQuantity + "×";
        String actualQuantityText = targetRow.findElement(By.xpath("td[1]")).getText().trim();
        assertEquals("Wrong quantity at row " + row, expectedQuantityText, actualQuantityText);

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
