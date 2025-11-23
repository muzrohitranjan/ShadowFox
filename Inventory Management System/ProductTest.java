import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1, "Laptop", "LAP001", 999.99, 10, "Electronics");
    }

    // Constructor and Basic Properties Tests (5 test cases)
    @Test
    void testProductCreation() {
        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("LAP001", product.getBarcode());
        assertEquals(999.99, product.getPrice(), 0.01);
        assertEquals(10, product.getQuantity());
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    void testProductWithZeroPrice() {
        Product freeProduct = new Product(2, "Free Sample", "FREE001", 0.0, 5, "Samples");
        assertEquals(0.0, freeProduct.getPrice(), 0.01);
        assertEquals(0.0, freeProduct.getTotalValue(), 0.01);
    }

    @Test
    void testProductWithZeroQuantity() {
        Product outOfStock = new Product(3, "Out of Stock", "OOS001", 50.0, 0, "Electronics");
        assertEquals(0, outOfStock.getQuantity());
        assertEquals(0.0, outOfStock.getTotalValue(), 0.01);
    }

    @Test
    void testProductWithLongName() {
        String longName = "Very Long Product Name That Exceeds Normal Length";
        Product longNameProduct = new Product(4, longName, "LONG001", 25.0, 3, "Test");
        assertEquals(longName, longNameProduct.getName());
    }

    @Test
    void testProductWithSpecialCharacters() {
        Product specialProduct = new Product(5, "Product & Co.", "SPEC@001", 15.99, 7, "Special/Category");
        assertEquals("Product & Co.", specialProduct.getName());
        assertEquals("SPEC@001", specialProduct.getBarcode());
        assertEquals("Special/Category", specialProduct.getCategory());
    }

    // Property Setters Tests (5 test cases)
    @Test
    void testSetName() {
        product.setName("Updated Laptop");
        assertEquals("Updated Laptop", product.getName());
    }

    @Test
    void testSetBarcode() {
        product.setBarcode("LAP002");
        assertEquals("LAP002", product.getBarcode());
    }

    @Test
    void testSetPrice() {
        product.setPrice(1299.99);
        assertEquals(1299.99, product.getPrice(), 0.01);
    }

    @Test
    void testSetQuantity() {
        product.setQuantity(25);
        assertEquals(25, product.getQuantity());
    }

    @Test
    void testSetCategory() {
        product.setCategory("Computers");
        assertEquals("Computers", product.getCategory());
    }

    // Total Value Calculation Tests (5 test cases)
    @Test
    void testGetTotalValue() {
        double expectedValue = 999.99 * 10;
        assertEquals(expectedValue, product.getTotalValue(), 0.01);
    }

    @Test
    void testTotalValueAfterPriceChange() {
        product.setPrice(500.0);
        assertEquals(5000.0, product.getTotalValue(), 0.01);
    }

    @Test
    void testTotalValueAfterQuantityChange() {
        product.setQuantity(5);
        assertEquals(4999.95, product.getTotalValue(), 0.01);
    }

    @Test
    void testTotalValueWithDecimalQuantity() {
        product.setPrice(33.33);
        product.setQuantity(3);
        assertEquals(99.99, product.getTotalValue(), 0.01);
    }

    @Test
    void testTotalValueZeroQuantity() {
        product.setQuantity(0);
        assertEquals(0.0, product.getTotalValue(), 0.01);
    }

    // JavaFX Properties Tests (5 test cases)
    @Test
    void testIdProperty() {
        assertNotNull(product.idProperty());
        assertEquals(1, product.idProperty().get());
        
        product.setId(100);
        assertEquals(100, product.idProperty().get());
    }

    @Test
    void testNameProperty() {
        assertNotNull(product.nameProperty());
        assertEquals("Laptop", product.nameProperty().get());
        
        product.nameProperty().set("Desktop");
        assertEquals("Desktop", product.getName());
    }

    @Test
    void testPriceProperty() {
        assertNotNull(product.priceProperty());
        assertEquals(999.99, product.priceProperty().get(), 0.01);
        
        product.priceProperty().set(1500.0);
        assertEquals(1500.0, product.getPrice(), 0.01);
    }

    @Test
    void testQuantityProperty() {
        assertNotNull(product.quantityProperty());
        assertEquals(10, product.quantityProperty().get());
        
        product.quantityProperty().set(20);
        assertEquals(20, product.getQuantity());
    }

    @Test
    void testCategoryProperty() {
        assertNotNull(product.categoryProperty());
        assertEquals("Electronics", product.categoryProperty().get());
        
        product.categoryProperty().set("Technology");
        assertEquals("Technology", product.getCategory());
    }

    // String Representation Tests (5 test cases)
    @Test
    void testToString() {
        String productString = product.toString();
        assertTrue(productString.contains("Laptop"));
        assertTrue(productString.contains("LAP001"));
        assertTrue(productString.contains("999.99"));
        assertTrue(productString.contains("10"));
        assertTrue(productString.contains("Electronics"));
    }

    @Test
    void testToStringWithZeroValues() {
        Product zeroProduct = new Product(0, "", "", 0.0, 0, "");
        String productString = zeroProduct.toString();
        assertTrue(productString.contains("id=0"));
        assertTrue(productString.contains("price=0.00"));
        assertTrue(productString.contains("quantity=0"));
    }

    @Test
    void testToStringFormat() {
        String productString = product.toString();
        assertTrue(productString.startsWith("Product{"));
        assertTrue(productString.endsWith("}"));
        assertTrue(productString.contains("id="));
        assertTrue(productString.contains("name="));
    }

    @Test
    void testToStringWithSpecialCharacters() {
        Product specialProduct = new Product(1, "Test & Product", "T&P001", 25.50, 5, "Test/Category");
        String productString = specialProduct.toString();
        assertTrue(productString.contains("Test & Product"));
        assertTrue(productString.contains("T&P001"));
    }

    @Test
    void testToStringConsistency() {
        String string1 = product.toString();
        String string2 = product.toString();
        assertEquals(string1, string2);
        
        product.setName("Changed Name");
        String string3 = product.toString();
        assertNotEquals(string1, string3);
    }
}