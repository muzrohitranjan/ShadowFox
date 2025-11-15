// Inventory Management System - Core Logic
class InventorySystem {
    constructor() {
        this.products = [];
        this.nextId = 1;
        this.lowStockThreshold = 10;
        this.activityLog = [];
        this.loadFromStorage();
        this.loadSampleData();
    }

    loadSampleData() {
        if (this.products.length === 0) {
            const sampleProducts = [
                { name: "Gaming Laptop", barcode: "LAP001", price: 1299.99, quantity: 8, category: "Electronics", description: "High-performance gaming laptop" },
                { name: "Wireless Mouse", barcode: "MOU001", price: 29.99, quantity: 45, category: "Electronics", description: "Ergonomic wireless mouse" },
                { name: "Mechanical Keyboard", barcode: "KEY001", price: 89.99, quantity: 22, category: "Electronics", description: "RGB mechanical keyboard" },
                { name: "4K Monitor", barcode: "MON001", price: 399.99, quantity: 12, category: "Electronics", description: "27-inch 4K display" },
                { name: "Office Chair", barcode: "CHA001", price: 199.99, quantity: 6, category: "Furniture", description: "Ergonomic office chair" },
                { name: "Standing Desk", barcode: "DSK001", price: 299.99, quantity: 4, category: "Furniture", description: "Adjustable standing desk" },
                { name: "Smartphone", barcode: "PHN001", price: 699.99, quantity: 18, category: "Electronics", description: "Latest smartphone model" },
                { name: "Tablet", barcode: "TAB001", price: 349.99, quantity: 25, category: "Electronics", description: "10-inch tablet" },
                { name: "Headphones", barcode: "HDP001", price: 149.99, quantity: 35, category: "Electronics", description: "Noise-canceling headphones" },
                { name: "Webcam", barcode: "CAM001", price: 79.99, quantity: 15, category: "Electronics", description: "HD webcam" }
            ];

            sampleProducts.forEach(product => this.addProduct(product));
        }
    }

    addProduct(productData) {
        if (this.findByBarcode(productData.barcode)) {
            return { success: false, message: "Barcode already exists" };
        }

        const product = {
            id: this.nextId++,
            name: productData.name,
            barcode: productData.barcode,
            price: parseFloat(productData.price),
            quantity: parseInt(productData.quantity),
            category: productData.category,
            description: productData.description || "",
            dateAdded: new Date(),
            lastModified: new Date()
        };

        this.products.push(product);
        this.logActivity(`Added product: ${product.name}`);
        this.saveToStorage();
        return { success: true, product };
    }

    updateProduct(id, productData) {
        const product = this.findById(id);
        if (!product) {
            return { success: false, message: "Product not found" };
        }

        const existingBarcode = this.findByBarcode(productData.barcode);
        if (existingBarcode && existingBarcode.id !== id) {
            return { success: false, message: "Barcode already exists" };
        }

        Object.assign(product, {
            name: productData.name,
            barcode: productData.barcode,
            price: parseFloat(productData.price),
            quantity: parseInt(productData.quantity),
            category: productData.category,
            description: productData.description || "",
            lastModified: new Date()
        });

        this.logActivity(`Updated product: ${product.name}`);
        this.saveToStorage();
        return { success: true, product };
    }

    deleteProduct(id) {
        const index = this.products.findIndex(p => p.id === id);
        if (index === -1) {
            return { success: false, message: "Product not found" };
        }

        const product = this.products[index];
        this.products.splice(index, 1);
        this.logActivity(`Deleted product: ${product.name}`);
        this.saveToStorage();
        return { success: true };
    }

    findById(id) {
        return this.products.find(p => p.id === id);
    }

    findByBarcode(barcode) {
        return this.products.find(p => p.barcode === barcode);
    }

    searchProducts(query) {
        const searchTerm = query.toLowerCase();
        return this.products.filter(product =>
            product.name.toLowerCase().includes(searchTerm) ||
            product.barcode.toLowerCase().includes(searchTerm) ||
            product.category.toLowerCase().includes(searchTerm)
        );
    }

    filterProducts(filters) {
        return this.products.filter(product => {
            if (filters.category && product.category !== filters.category) return false;
            if (filters.minQuantity !== undefined && product.quantity < filters.minQuantity) return false;
            if (filters.maxQuantity !== undefined && product.quantity > filters.maxQuantity) return false;
            if (filters.minPrice !== undefined && product.price < filters.minPrice) return false;
            if (filters.maxPrice !== undefined && product.price > filters.maxPrice) return false;
            return true;
        });
    }

    getLowStockProducts() {
        return this.products.filter(p => p.quantity <= this.lowStockThreshold);
    }

    getCategories() {
        return [...new Set(this.products.map(p => p.category))].sort();
    }

    getStatistics() {
        const totalProducts = this.products.length;
        const totalQuantity = this.products.reduce((sum, p) => sum + p.quantity, 0);
        const totalValue = this.products.reduce((sum, p) => sum + (p.price * p.quantity), 0);
        const avgPrice = totalProducts > 0 ? totalValue / totalQuantity : 0;
        const lowStockCount = this.getLowStockProducts().length;
        const categories = this.getCategories();

        return {
            totalProducts,
            totalQuantity,
            totalValue,
            avgPrice,
            lowStockCount,
            categoriesCount: categories.length,
            categories
        };
    }

    getCategoryAnalysis() {
        const categories = {};
        this.products.forEach(product => {
            if (!categories[product.category]) {
                categories[product.category] = {
                    count: 0,
                    totalValue: 0,
                    totalQuantity: 0
                };
            }
            categories[product.category].count++;
            categories[product.category].totalValue += product.price * product.quantity;
            categories[product.category].totalQuantity += product.quantity;
        });
        return categories;
    }

    getPriceAnalysis() {
        if (this.products.length === 0) return {};

        const prices = this.products.map(p => p.price);
        const minPrice = Math.min(...prices);
        const maxPrice = Math.max(...prices);
        const avgPrice = prices.reduce((sum, price) => sum + price, 0) / prices.length;

        const priceRanges = {
            "Under $50": this.products.filter(p => p.price < 50).length,
            "$50 - $100": this.products.filter(p => p.price >= 50 && p.price < 100).length,
            "$100 - $500": this.products.filter(p => p.price >= 100 && p.price < 500).length,
            "$500+": this.products.filter(p => p.price >= 500).length
        };

        return { minPrice, maxPrice, avgPrice, priceRanges };
    }

    generateBarcode() {
        const prefix = "PRD";
        const number = String(this.nextId).padStart(4, '0');
        return `${prefix}${number}`;
    }

    logActivity(message) {
        this.activityLog.unshift({
            message,
            timestamp: new Date(),
            id: Date.now()
        });
        
        // Keep only last 50 activities
        if (this.activityLog.length > 50) {
            this.activityLog = this.activityLog.slice(0, 50);
        }
    }

    getRecentActivity(limit = 10) {
        return this.activityLog.slice(0, limit);
    }

    exportData() {
        return {
            products: this.products,
            nextId: this.nextId,
            activityLog: this.activityLog,
            exportDate: new Date()
        };
    }

    importData(data) {
        try {
            this.products = data.products || [];
            this.nextId = data.nextId || 1;
            this.activityLog = data.activityLog || [];
            this.saveToStorage();
            return { success: true };
        } catch (error) {
            return { success: false, message: "Invalid data format" };
        }
    }

    clearAllData() {
        this.products = [];
        this.nextId = 1;
        this.activityLog = [];
        this.saveToStorage();
        this.logActivity("All data cleared");
    }

    saveToStorage() {
        const data = {
            products: this.products,
            nextId: this.nextId,
            lowStockThreshold: this.lowStockThreshold,
            activityLog: this.activityLog
        };
        localStorage.setItem('inventorySystem', JSON.stringify(data));
    }

    loadFromStorage() {
        const data = localStorage.getItem('inventorySystem');
        if (data) {
            try {
                const parsed = JSON.parse(data);
                this.products = parsed.products || [];
                this.nextId = parsed.nextId || 1;
                this.lowStockThreshold = parsed.lowStockThreshold || 10;
                this.activityLog = parsed.activityLog || [];
            } catch (error) {
                console.error('Error loading data from storage:', error);
            }
        }
    }
}

// Global inventory system instance
const inventory = new InventorySystem();