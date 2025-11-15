import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {
    private ObservableList<Product> products;
    private int nextId;

    public InventoryManager() {
        this.products = FXCollections.observableArrayList();
        this.nextId = 1;
        loadSampleData();
    }

    private void loadSampleData() {
        addProduct(new Product(nextId++, "Laptop", "LAP001", 999.99, 10, "Electronics"));
        addProduct(new Product(nextId++, "Mouse", "MOU001", 25.50, 50, "Electronics"));
        addProduct(new Product(nextId++, "Keyboard", "KEY001", 75.00, 30, "Electronics"));
        addProduct(new Product(nextId++, "Monitor", "MON001", 299.99, 15, "Electronics"));
        addProduct(new Product(nextId++, "Desk Chair", "CHA001", 150.00, 8, "Furniture"));
    }

    public boolean addProduct(Product product) {
        if (product == null || findByBarcode(product.getBarcode()) != null) {
            return false;
        }
        if (product.getId() == 0) {
            product.setId(nextId++);
        }
        return products.add(product);
    }

    public boolean updateProduct(Product product) {
        if (product == null) return false;
        
        Product existing = findById(product.getId());
        if (existing == null) return false;
        
        // Check if barcode is unique (excluding current product)
        Product barcodeCheck = findByBarcode(product.getBarcode());
        if (barcodeCheck != null && barcodeCheck.getId() != product.getId()) {
            return false;
        }
        
        existing.setName(product.getName());
        existing.setBarcode(product.getBarcode());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setCategory(product.getCategory());
        return true;
    }

    public boolean deleteProduct(int id) {
        return products.removeIf(product -> product.getId() == id);
    }

    public Product findById(int id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Product findByBarcode(String barcode) {
        return products.stream()
                .filter(product -> product.getBarcode().equals(barcode))
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchByName(String name) {
        return products.stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> filterByCategory(String category) {
        return products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> filterByQuantity(int minQuantity, int maxQuantity) {
        return products.stream()
                .filter(product -> product.getQuantity() >= minQuantity && product.getQuantity() <= maxQuantity)
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
                .filter(product -> product.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    public double getTotalStockValue() {
        return products.stream()
                .mapToDouble(Product::getTotalValue)
                .sum();
    }

    public double getCategoryValue(String category) {
        return products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Product::getTotalValue)
                .sum();
    }

    public List<String> getAllCategories() {
        return products.stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public int getTotalProducts() {
        return products.size();
    }

    public int getTotalQuantity() {
        return products.stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }
}