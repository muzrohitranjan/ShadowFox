import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class ProductFormDialog extends Dialog<Product> {
    private TextField nameField;
    private TextField barcodeField;
    private TextField priceField;
    private TextField quantityField;
    private ComboBox<String> categoryCombo;
    private Product product;

    public ProductFormDialog(Product product, InventoryManager inventoryManager) {
        this.product = product;
        
        setTitle(product == null ? "Add Product" : "Edit Product");
        setHeaderText(product == null ? "Enter product details" : "Edit product details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createForm(inventoryManager);
        getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidInput());
        });

        barcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidInput());
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidInput());
        });

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidInput());
        });

        // Populate fields if editing
        if (product != null) {
            populateFields();
            saveButton.setDisable(false);
        }

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createProductFromForm();
            }
            return null;
        });
    }

    private GridPane createForm(InventoryManager inventoryManager) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        nameField = new TextField();
        nameField.setPromptText("Product Name");
        
        barcodeField = new TextField();
        barcodeField.setPromptText("Barcode (e.g., PRD001)");
        
        priceField = new TextField();
        priceField.setPromptText("Price");
        
        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        categoryCombo = new ComboBox<>();
        categoryCombo.setEditable(true);
        categoryCombo.getItems().addAll(inventoryManager.getAllCategories());
        if (categoryCombo.getItems().isEmpty()) {
            categoryCombo.getItems().addAll("Electronics", "Furniture", "Clothing", "Books", "Food");
        }
        categoryCombo.setPromptText("Select or enter category");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Barcode:"), 0, 1);
        grid.add(barcodeField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);

        return grid;
    }

    private void populateFields() {
        nameField.setText(product.getName());
        barcodeField.setText(product.getBarcode());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        categoryCombo.setValue(product.getCategory());
    }

    private boolean isValidInput() {
        try {
            String name = nameField.getText().trim();
            String barcode = barcodeField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String category = categoryCombo.getValue();

            if (name.isEmpty() || barcode.isEmpty() || priceText.isEmpty() || 
                quantityText.isEmpty() || category == null || category.trim().isEmpty()) {
                return false;
            }

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            return price >= 0 && quantity >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Product createProductFromForm() {
        try {
            String name = nameField.getText().trim();
            String barcode = barcodeField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String category = categoryCombo.getValue().trim();

            if (product == null) {
                return new Product(0, name, barcode, price, quantity, category);
            } else {
                product.setName(name);
                product.setBarcode(barcode);
                product.setPrice(price);
                product.setQuantity(quantity);
                product.setCategory(category);
                return product;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}