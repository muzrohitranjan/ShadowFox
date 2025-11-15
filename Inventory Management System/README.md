# Inventory Management System

A comprehensive JavaFX-based inventory management system with GUI for managing product stock, prices, and quantities.

## 🚀 Features

### Core Functionality
- ✅ **Add Products**: Create new inventory items with validation
- ✅ **Edit Products**: Update existing product information
- ✅ **Delete Products**: Remove products with confirmation
- ✅ **Search & Filter**: Find products by name, category, quantity range
- ✅ **Barcode Management**: Manual barcode entry and auto-generation
- ✅ **Real-time Statistics**: Total value, quantity, and product counts

### Web Frontend Features
- 🌐 **Modern Web Interface**: Responsive design with professional UI
- 📊 **Interactive Dashboard**: Real-time statistics and analytics
- 📈 **Charts & Graphs**: Visual data representation with Chart.js
- 🔍 **Advanced Search**: Real-time filtering and search capabilities
- 📱 **Mobile Responsive**: Works on desktop, tablet, and mobile
- 💾 **Data Persistence**: LocalStorage with import/export functionality

### Advanced Features
- ✅ **Low Stock Alerts**: Automatic detection of products with low inventory
- ✅ **Category Management**: Dynamic category creation and filtering
- ✅ **Quantity Filters**: Filter products by stock levels
- ✅ **Total Stock Value Calculator**: Real-time inventory valuation
- ✅ **Data Validation**: Comprehensive input validation and error handling
- ✅ **Responsive GUI**: Modern JavaFX interface with table views

### GUI Components
- 📊 **TableView**: Display products with sortable columns
- 🔍 **Search Bar**: Real-time product search
- 📝 **Form Dialogs**: Add/Edit product forms with validation
- 📈 **Statistics Panel**: Live inventory metrics
- 🎛️ **Filter Controls**: Category and quantity range filters
- ⚠️ **Alert System**: Low stock warnings and notifications

## 🏗️ Project Structure

```
InventoryManagementSystem/
├── src/
│   ├── main/java/
│   │   ├── Product.java              # Product model with JavaFX properties
│   │   ├── InventoryManager.java     # Business logic and data management
│   │   ├── ProductFormDialog.java    # Add/Edit product dialog
│   │   └── InventoryApp.java         # Main JavaFX application
│   └── test/java/
│       ├── ProductTest.java          # Product model tests (25+ cases)
│       └── InventoryManagerTest.java # Business logic tests (25+ cases)
├── pom.xml                          # Maven configuration
├── run-app.bat                      # Application launcher
└── README.md                        # This file
```

## 🛠️ Technologies Used

- **JavaFX 17**: Modern GUI framework
- **Java 11+**: Core programming language
- **Maven**: Build and dependency management
- **JUnit 5**: Unit testing framework
- **JavaFX Properties**: Data binding and reactive UI

## 🚀 How to Run

### Option 1: Using Maven (Recommended)
```bash
mvn clean javafx:run
```

### Option 2: Using Batch File
```bash
# Double-click run-app.bat or run in command prompt
run-app.bat
```

### Option 3: Manual Compilation
```bash
# Compile
javac --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp src/main/java src/main/java/*.java

# Run
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp src/main/java InventoryApp
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductTest
mvn test -Dtest=InventoryManagerTest
```

## 📋 Test Coverage (50+ Test Cases)

### ProductTest.java (25+ test cases)
- **Constructor & Properties**: Product creation, property validation
- **Setters & Getters**: Data modification and retrieval
- **Total Value Calculation**: Price × quantity calculations
- **JavaFX Properties**: Property binding and reactive updates
- **String Representation**: toString() method validation

### InventoryManagerTest.java (25+ test cases)
- **CRUD Operations**: Add, update, delete products
- **Search & Filter**: Name search, category filtering, quantity ranges
- **Data Validation**: Duplicate barcode prevention, null checks
- **Statistics**: Total value, quantity calculations, category analysis
- **Edge Cases**: Empty inventory, boundary conditions

## 🎯 Key Learning Outcomes

1. **JavaFX GUI Development**: TableView, Forms, Dialogs, Event Handling
2. **Data Binding**: JavaFX Properties for reactive UI updates
3. **CRUD Operations**: Complete Create, Read, Update, Delete functionality
4. **Input Validation**: Form validation and error handling
5. **Search & Filtering**: Real-time data filtering and search
6. **Unit Testing**: Comprehensive test coverage with JUnit 5
7. **Software Architecture**: MVC pattern, separation of concerns
8. **User Experience**: Intuitive GUI design and user feedback

## 📊 Sample Data

The application comes pre-loaded with sample products:
- Laptop (Electronics) - $999.99, Qty: 10
- Mouse (Electronics) - $25.50, Qty: 50
- Keyboard (Electronics) - $75.00, Qty: 30
- Monitor (Electronics) - $299.99, Qty: 15
- Desk Chair (Furniture) - $150.00, Qty: 8

## 🔧 System Requirements

- **Java 11 or higher**
- **JavaFX 17** (included in dependencies)
- **Maven 3.6+** (optional, for build management)
- **Windows/Linux/macOS** (cross-platform)

## 🎨 GUI Features

### Main Window
- **Toolbar**: Add, Edit, Delete, Refresh, Low Stock Alert buttons
- **Filter Panel**: Search field, category dropdown, quantity range inputs
- **Product Table**: Sortable columns with color-coded low stock items
- **Statistics Panel**: Real-time inventory metrics

### Product Form Dialog
- **Input Fields**: Name, Barcode, Price, Quantity, Category
- **Validation**: Real-time input validation with error feedback
- **Category Management**: Dropdown with existing categories + custom entry
- **Save/Cancel**: Form submission with confirmation

### Features in Action
- **Low Stock Highlighting**: Products with ≤10 units shown in red
- **Real-time Filtering**: Instant results as you type
- **Confirmation Dialogs**: Safe delete operations
- **Error Handling**: User-friendly error messages

## 🚀 Advanced Features Implemented

- **Barcode Validation**: Prevents duplicate barcodes
- **Dynamic Categories**: Auto-populate from existing products
- **Quantity Range Filters**: Min/max quantity filtering
- **Total Stock Value**: Real-time inventory valuation
- **Low Stock Alerts**: Configurable threshold warnings
- **Search Functionality**: Case-insensitive name search
- **Data Persistence**: In-memory storage with sample data
- **Responsive Design**: Resizable interface components

## 🔮 Future Enhancements

- [ ] Database integration (SQLite/MySQL)
- [ ] Barcode scanner integration
- [ ] Export to CSV/Excel
- [ ] Product images and descriptions
- [ ] Supplier management
- [ ] Purchase order generation
- [ ] Inventory movement tracking
- [ ] Multi-user support with authentication
- [ ] REST API for mobile app integration
- [ ] Advanced reporting and analytics

This inventory management system demonstrates professional-level JavaFX development with comprehensive testing, modern GUI design, and robust business logic!