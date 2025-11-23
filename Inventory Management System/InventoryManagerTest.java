import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class InventoryManagerTest {
    private InventoryManager inventoryManager;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        inventoryManager = new InventoryManager();
        testProduct = new Product(0, "Test Product", "TEST001", 50.0, 20, "Test Category");
    }

    // Add Product Tests (5 test cases)
    @Test
    void testAddValidProduct() {
        assertTrue(inventoryManager.addProduct(testProduct));
        assertNotNull(inventoryManager.findByBarcode("TEST001"));
        assertTrue(testProduct.getId() > 0); // ID should be assigned
    }

    @Test
    void testAddNullProduct() {
        assertFalse(inventoryManager.addProduct(null));
    }

    @Test
    void testAddDuplicateBarcode() {
        inventoryManager.addProduct(testProduct);
        Product duplicate = new Product(0, "Duplicate", "TEST001", 30.0, 10, "Test");
        assertFalse(inventoryManager.addProduct(duplicate));
    }

    @Test
    void testAddProductWithExistingId() {
        testProduct.setId(999);
        assertTrue(inventoryManager.addProduct(testProduct));
        assertEquals(999, testProduct.getId()); // Should keep existing ID
    }

    @Test
    void testAddMultipleProducts() {
        Product product1 = new Product(0, "Product 1", "P001", 10.0, 5, "Category1");
        Product product2 = new Product(0, "Product 2", "P002", 20.0, 10, "Category2");
        
        assertTrue(inventoryManager.addProduct(product1));
        assertTrue(inventoryManager.addProduct(product2));
        assertEquals(2, inventoryManager.getProducts().size() - 5); // -5 for sample data
    }

    // Update Product Tests (5 test cases)
    @Test
    void testUpdateValidProduct() {
        inventoryManager.addProduct(testProduct);
        
        testProduct.setName("Updated Product");
        testProduct.setPrice(75.0);
        
        assertTrue(inventoryManager.updateProduct(testProduct));
        
        Product updated = inventoryManager.findById(testProduct.getId());
        assertEquals("Updated Product", updated.getName());
        assertEquals(75.0, updated.getPrice(), 0.01);
    }

    @Test
    void testUpdateNullProduct() {
        assertFalse(inventoryManager.updateProduct(null));
    }

    @Test
    void testUpdateNonExistentProduct() {
        Product nonExistent = new Product(9999, "Non-existent", "NE001", 10.0, 5, "Test");
        assertFalse(inventoryManager.updateProduct(nonExistent));
    }

    @Test
    void testUpdateWithDuplicateBarcode() {
        Product product1 = new Product(0, "Product 1", "P001", 10.0, 5, "Test");
        Product product2 = new Product(0, "Product 2", "P002", 20.0, 10, "Test");
        
        inventoryManager.addProduct(product1);
        inventoryManager.addProduct(product2);
        
        product2.setBarcode("P001"); // Try to use existing barcode
        assertFalse(inventoryManager.updateProduct(product2));
    }

    @Test
    void testUpdateSameBarcodeAllowed() {
        inventoryManager.addProduct(testProduct);
        
        testProduct.setName("Updated Name");
        // Keep same barcode
        
        assertTrue(inventoryManager.updateProduct(testProduct));
    }

    // Delete Product Tests (5 test cases)
    @Test
    void testDeleteExistingProduct() {
        inventoryManager.addProduct(testProduct);
        int productId = testProduct.getId();
        
        assertTrue(inventoryManager.deleteProduct(productId));
        assertNull(inventoryManager.findById(productId));
    }

    @Test
    void testDeleteNonExistentProduct() {
        assertFalse(inventoryManager.deleteProduct(9999));
    }

    @Test
    void testDeleteMultipleProducts() {
        Product product1 = new Product(0, "Product 1", "P001", 10.0, 5, "Test");
        Product product2 = new Product(0, "Product 2", "P002", 20.0, 10, "Test");
        
        inventoryManager.addProduct(product1);
        inventoryManager.addProduct(product2);
        
        assertTrue(inventoryManager.deleteProduct(product1.getId()));
        assertTrue(inventoryManager.deleteProduct(product2.getId()));
        
        assertNull(inventoryManager.findById(product1.getId()));
        assertNull(inventoryManager.findById(product2.getId()));
    }

    @Test
    void testDeleteFromSampleData() {
        // Sample data should be loaded
        assertTrue(inventoryManager.getTotalProducts() > 0);
        
        Product sampleProduct = inventoryManager.getProducts().get(0);
        assertTrue(inventoryManager.deleteProduct(sampleProduct.getId()));
    }

    @Test
    void testDeleteAllProducts() {
        var allProducts = inventoryManager.getProducts();
        var productIds = allProducts.stream().mapToInt(Product::getId).toArray();
        
        for (int id : productIds) {
            inventoryManager.deleteProduct(id);
        }
        
        assertEquals(0, inventoryManager.getTotalProducts());
    }

    // Search and Find Tests (5 test cases)
    @Test
    void testFindById() {
        inventoryManager.addProduct(testProduct);
        
        Product found = inventoryManager.findById(testProduct.getId());
        assertNotNull(found);
        assertEquals(testProduct.getName(), found.getName());
    }

    @Test
    void testFindByBarcode() {
        inventoryManager.addProduct(testProduct);
        
        Product found = inventoryManager.findByBarcode("TEST001");
        assertNotNull(found);
        assertEquals(testProduct.getName(), found.getName());
    }

    @Test
    void testSearchByName() {
        Product product1 = new Product(0, "Gaming Laptop", "GL001", 1500.0, 5, "Electronics");
        Product product2 = new Product(0, "Office Laptop", "OL001", 800.0, 10, "Electronics");
        Product product3 = new Product(0, "Gaming Mouse", "GM001", 50.0, 20, "Electronics");
        
        inventoryManager.addProduct(product1);
        inventoryManager.addProduct(product2);
        inventoryManager.addProduct(product3);
        
        List<Product> laptops = inventoryManager.searchByName("laptop");
        assertEquals(2, laptops.size());
        
        List<Product> gaming = inventoryManager.searchByName("gaming");
        assertEquals(2, gaming.size());
    }

    @Test
    void testSearchByNameCaseInsensitive() {
        inventoryManager.addProduct(testProduct);
        
        List<Product> results1 = inventoryManager.searchByName("test");
        List<Product> results2 = inventoryManager.searchByName("TEST");
        List<Product> results3 = inventoryManager.searchByName("Test");
        
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
    }

    @Test
    void testSearchByNameNoResults() {
        List<Product> results = inventoryManager.searchByName("NonExistentProduct");
        assertTrue(results.isEmpty());
    }

    // Filter Tests (5 test cases)
    @Test
    void testFilterByCategory() {
        Product electronics1 = new Product(0, "Phone", "PH001", 500.0, 15, "Electronics");
        Product electronics2 = new Product(0, "Tablet", "TB001", 300.0, 8, "Electronics");
        Product furniture = new Product(0, "Chair", "CH001", 150.0, 12, "Furniture");
        
        inventoryManager.addProduct(electronics1);
        inventoryManager.addProduct(electronics2);
        inventoryManager.addProduct(furniture);
        
        List<Product> electronicsProducts = inventoryManager.filterByCategory("Electronics");
        assertTrue(electronicsProducts.size() >= 2); // At least our 2 + sample data
        
        List<Product> furnitureProducts = inventoryManager.filterByCategory("Furniture");
        assertTrue(furnitureProducts.size() >= 1);
    }

    @Test
    void testFilterByQuantityRange() {
        Product lowStock = new Product(0, "Low Stock", "LS001", 10.0, 3, "Test");
        Product mediumStock = new Product(0, "Medium Stock", "MS001", 20.0, 15, "Test");
        Product highStock = new Product(0, "High Stock", "HS001", 30.0, 50, "Test");
        
        inventoryManager.addProduct(lowStock);
        inventoryManager.addProduct(mediumStock);
        inventoryManager.addProduct(highStock);
        
        List<Product> lowToMedium = inventoryManager.filterByQuantity(1, 20);
        assertTrue(lowToMedium.size() >= 2);
        
        List<Product> highOnly = inventoryManager.filterByQuantity(40, 100);
        assertTrue(highOnly.size() >= 1);
    }

    @Test
    void testGetLowStockProducts() {
        Product lowStock1 = new Product(0, "Low Stock 1", "LS001", 10.0, 5, "Test");
        Product lowStock2 = new Product(0, "Low Stock 2", "LS002", 20.0, 3, "Test");
        Product normalStock = new Product(0, "Normal Stock", "NS001", 30.0, 25, "Test");
        
        inventoryManager.addProduct(lowStock1);
        inventoryManager.addProduct(lowStock2);
        inventoryManager.addProduct(normalStock);
        
        List<Product> lowStockProducts = inventoryManager.getLowStockProducts(10);
        assertTrue(lowStockProducts.size() >= 2);
    }

    @Test
    void testGetAllCategories() {
        Product category1Product = new Product(0, "Product 1", "P001", 10.0, 5, "Category A");
        Product category2Product = new Product(0, "Product 2", "P002", 20.0, 10, "Category B");
        
        inventoryManager.addProduct(category1Product);
        inventoryManager.addProduct(category2Product);
        
        List<String> categories = inventoryManager.getAllCategories();
        assertTrue(categories.contains("Category A"));
        assertTrue(categories.contains("Category B"));
        assertTrue(categories.size() >= 2);
    }

    @Test
    void testFilterByCategoryCaseInsensitive() {
        Product product = new Product(0, "Test Product", "TP001", 25.0, 10, "Electronics");
        inventoryManager.addProduct(product);
        
        List<Product> results1 = inventoryManager.filterByCategory("electronics");
        List<Product> results2 = inventoryManager.filterByCategory("ELECTRONICS");
        List<Product> results3 = inventoryManager.filterByCategory("Electronics");
        
        assertTrue(results1.size() >= 1);
        assertTrue(results2.size() >= 1);
        assertTrue(results3.size() >= 1);
    }

    // Statistics Tests (5 test cases)
    @Test
    void testGetTotalStockValue() {
        // Clear existing products for accurate calculation
        var existingProducts = inventoryManager.getProducts();
        existingProducts.clear();
        
        Product product1 = new Product(0, "Product 1", "P001", 10.0, 5, "Test"); // Value: 50
        Product product2 = new Product(0, "Product 2", "P002", 20.0, 3, "Test"); // Value: 60
        
        inventoryManager.addProduct(product1);
        inventoryManager.addProduct(product2);
        
        assertEquals(110.0, inventoryManager.getTotalStockValue(), 0.01);
    }

    @Test
    void testGetCategoryValue() {
        Product electronics1 = new Product(0, "Phone", "PH001", 100.0, 2, "Electronics"); // Value: 200
        Product electronics2 = new Product(0, "Tablet", "TB001", 150.0, 1, "Electronics"); // Value: 150
        Product furniture = new Product(0, "Chair", "CH001", 75.0, 4, "Furniture"); // Value: 300
        
        inventoryManager.addProduct(electronics1);
        inventoryManager.addProduct(electronics2);
        inventoryManager.addProduct(furniture);
        
        assertEquals(350.0, inventoryManager.getCategoryValue("Electronics"), 0.01);
        assertEquals(300.0, inventoryManager.getCategoryValue("Furniture"), 0.01);
    }

    @Test
    void testGetTotalProducts() {
        int initialCount = inventoryManager.getTotalProducts();
        
        inventoryManager.addProduct(testProduct);
        assertEquals(initialCount + 1, inventoryManager.getTotalProducts());
        
        inventoryManager.deleteProduct(testProduct.getId());
        assertEquals(initialCount, inventoryManager.getTotalProducts());
    }

    @Test
    void testGetTotalQuantity() {
        // Clear existing for accurate count
        var existingProducts = inventoryManager.getProducts();
        int initialQuantity = existingProducts.stream().mapToInt(Product::getQuantity).sum();
        
        Product product = new Product(0, "Test", "T001", 10.0, 25, "Test");
        inventoryManager.addProduct(product);
        
        assertEquals(initialQuantity + 25, inventoryManager.getTotalQuantity());
    }

    @Test
    void testEmptyInventoryStatistics() {
        // Clear all products
        var allProducts = inventoryManager.getProducts();
        var productIds = allProducts.stream().mapToInt(Product::getId).toArray();
        
        for (int id : productIds) {
            inventoryManager.deleteProduct(id);
        }
        
        assertEquals(0, inventoryManager.getTotalProducts());
        assertEquals(0, inventoryManager.getTotalQuantity());
        assertEquals(0.0, inventoryManager.getTotalStockValue(), 0.01);
        assertTrue(inventoryManager.getAllCategories().isEmpty());
    }
}