import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

public class InventoryApp extends Application {
    private InventoryManager inventoryManager;
    private TableView<Product> productTable;
    private FilteredList<Product> filteredProducts;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private TextField minQuantityField;
    private TextField maxQuantityField;
    private Label totalValueLabel;
    private Label totalProductsLabel;
    private Label totalQuantityLabel;

    @Override
    public void start(Stage primaryStage) {
        inventoryManager = new InventoryManager();
        
        primaryStage.setTitle("Inventory Management System");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        // Create components
        root.getChildren().addAll(
            createToolbar(),
            createFilterPanel(),
            createTableView(),
            createStatsPanel()
        );
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        updateStats();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(5));
        
        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> showAddProductDialog());
        
        Button editButton = new Button("Edit Product");
        editButton.setOnAction(e -> showEditProductDialog());
        
        Button deleteButton = new Button("Delete Product");
        deleteButton.setOnAction(e -> deleteSelectedProduct());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshTable());
        
        Button lowStockButton = new Button("Low Stock Alert");
        lowStockButton.setOnAction(e -> showLowStockAlert());
        
        toolbar.getChildren().addAll(addButton, editButton, deleteButton, 
                                   new Separator(), refreshButton, lowStockButton);
        
        return toolbar;
    }

    private VBox createFilterPanel() {
        VBox filterPanel = new VBox(5);
        filterPanel.setPadding(new Insets(5));
        
        Label filterLabel = new Label("Filters:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(inventoryManager.getAllCategories());
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(e -> applyFilters());
        
        searchBox.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Category:"), categoryFilter
        );
        
        HBox quantityBox = new HBox(10);
        minQuantityField = new TextField();
        minQuantityField.setPromptText("Min");
        minQuantityField.setPrefWidth(80);
        minQuantityField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        maxQuantityField = new TextField();
        maxQuantityField.setPromptText("Max");
        maxQuantityField.setPrefWidth(80);
        maxQuantityField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(e -> clearFilters());
        
        quantityBox.getChildren().addAll(
            new Label("Quantity Range:"), minQuantityField, new Label("to"), maxQuantityField,
            clearFiltersButton
        );
        
        filterPanel.getChildren().addAll(filterLabel, searchBox, quantityBox);
        return filterPanel;
    }

    private TableView<Product> createTableView() {
        productTable = new TableView<>();
        
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Product, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        barcodeCol.setPrefWidth(100);
        
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);
        priceCol.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(80);
        quantityCol.setCellFactory(col -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(quantity.toString());
                    if (quantity <= 10) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);
        
        TableColumn<Product, Double> totalValueCol = new TableColumn<>("Total Value");
        totalValueCol.setCellValueFactory(cellData -> 
            cellData.getValue().priceProperty().multiply(cellData.getValue().quantityProperty()).asObject());
        totalValueCol.setPrefWidth(100);
        totalValueCol.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", value));
                }
            }
        });
        
        productTable.getColumns().addAll(idCol, nameCol, barcodeCol, priceCol, 
                                       quantityCol, categoryCol, totalValueCol);
        
        filteredProducts = new FilteredList<>(inventoryManager.getProducts());
        productTable.setItems(filteredProducts);
        
        return productTable;
    }

    private HBox createStatsPanel() {
        HBox statsPanel = new HBox(20);
        statsPanel.setPadding(new Insets(10));
        statsPanel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc;");
        
        totalProductsLabel = new Label();
        totalQuantityLabel = new Label();
        totalValueLabel = new Label();
        
        statsPanel.getChildren().addAll(totalProductsLabel, totalQuantityLabel, totalValueLabel);
        return statsPanel;
    }

    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(null, inventoryManager);
        Optional<Product> result = dialog.showAndWait();
        
        result.ifPresent(product -> {
            if (inventoryManager.addProduct(product)) {
                refreshTable();
                showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to add product. Barcode might already exist.", Alert.AlertType.ERROR);
            }
        });
    }

    private void showEditProductDialog() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product to edit.", Alert.AlertType.WARNING);
            return;
        }
        
        ProductFormDialog dialog = new ProductFormDialog(selected, inventoryManager);
        Optional<Product> result = dialog.showAndWait();
        
        result.ifPresent(product -> {
            if (inventoryManager.updateProduct(product)) {
                refreshTable();
                showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update product. Barcode might already exist.", Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product to delete.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Product");
        confirmAlert.setContentText("Are you sure you want to delete: " + selected.getName() + "?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (inventoryManager.deleteProduct(selected.getId())) {
                refreshTable();
                showAlert("Success", "Product deleted successfully!", Alert.AlertType.INFORMATION);
            }
        }
    }

    private void applyFilters() {
        filteredProducts.setPredicate(product -> {
            // Search filter
            String searchText = searchField.getText().toLowerCase();
            if (!searchText.isEmpty() && !product.getName().toLowerCase().contains(searchText)) {
                return false;
            }
            
            // Category filter
            String selectedCategory = categoryFilter.getValue();
            if (!"All Categories".equals(selectedCategory) && !product.getCategory().equals(selectedCategory)) {
                return false;
            }
            
            // Quantity range filter
            try {
                String minText = minQuantityField.getText();
                String maxText = maxQuantityField.getText();
                
                if (!minText.isEmpty()) {
                    int minQuantity = Integer.parseInt(minText);
                    if (product.getQuantity() < minQuantity) return false;
                }
                
                if (!maxText.isEmpty()) {
                    int maxQuantity = Integer.parseInt(maxText);
                    if (product.getQuantity() > maxQuantity) return false;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
            
            return true;
        });
        
        updateStats();
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.setValue("All Categories");
        minQuantityField.clear();
        maxQuantityField.clear();
    }

    private void refreshTable() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(inventoryManager.getAllCategories());
        categoryFilter.setValue("All Categories");
        
        productTable.refresh();
        updateStats();
    }

    private void updateStats() {
        double totalValue = filteredProducts.stream().mapToDouble(Product::getTotalValue).sum();
        int totalProducts = filteredProducts.size();
        int totalQuantity = filteredProducts.stream().mapToInt(Product::getQuantity).sum();
        
        totalProductsLabel.setText("Products: " + totalProducts);
        totalQuantityLabel.setText("Total Quantity: " + totalQuantity);
        totalValueLabel.setText("Total Value: $" + String.format("%.2f", totalValue));
    }

    private void showLowStockAlert() {
        var lowStockProducts = inventoryManager.getLowStockProducts(10);
        
        if (lowStockProducts.isEmpty()) {
            showAlert("Low Stock Alert", "No products with low stock (≤10 units).", Alert.AlertType.INFORMATION);
        } else {
            StringBuilder message = new StringBuilder("Products with low stock (≤10 units):\n\n");
            for (Product product : lowStockProducts) {
                message.append(String.format("• %s (Qty: %d)\n", product.getName(), product.getQuantity()));
            }
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Low Stock Alert");
            alert.setHeaderText("Low Stock Products Found");
            alert.setContentText(message.toString());
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}