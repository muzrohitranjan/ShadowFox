import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty barcode;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final StringProperty category;

    public Product(int id, String name, String barcode, double price, int quantity, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.barcode = new SimpleStringProperty(barcode);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.category = new SimpleStringProperty(category);
    }

    // Property getters for JavaFX binding
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty barcodeProperty() { return barcode; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty categoryProperty() { return category; }

    // Value getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getBarcode() { return barcode.get(); }
    public double getPrice() { return price.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getCategory() { return category.get(); }

    // Value setters
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setBarcode(String barcode) { this.barcode.set(barcode); }
    public void setPrice(double price) { this.price.set(price); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setCategory(String category) { this.category.set(category); }

    public double getTotalValue() {
        return price.get() * quantity.get();
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', barcode='%s', price=%.2f, quantity=%d, category='%s'}",
                getId(), getName(), getBarcode(), getPrice(), getQuantity(), getCategory());
    }
}