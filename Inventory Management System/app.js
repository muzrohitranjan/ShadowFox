// Main Application Logic
class InventoryApp {
    constructor() {
        this.currentSection = 'dashboard';
        this.currentSort = { column: null, direction: 'asc' };
        this.filteredProducts = [];
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.updateUI();
        this.showSection('dashboard');
    }

    setupEventListeners() {
        // Navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const section = e.target.dataset.section;
                this.showSection(section);
            });
        });

        // Search and filters
        document.getElementById('search-input')?.addEventListener('input', () => this.applyFilters());
        document.getElementById('category-filter')?.addEventListener('change', () => this.applyFilters());
        document.getElementById('min-quantity')?.addEventListener('input', () => this.applyFilters());
        document.getElementById('max-quantity')?.addEventListener('input', () => this.applyFilters());

        // Product form
        document.getElementById('product-form')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleProductSubmit();
        });

        // Modal form
        document.getElementById('modal-product-form')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveProduct();
        });

        // Settings
        document.getElementById('low-stock-threshold')?.addEventListener('change', (e) => {
            inventory.lowStockThreshold = parseInt(e.target.value);
            inventory.saveToStorage();
            this.updateUI();
        });
    }

    showSection(sectionName) {
        // Update navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-section="${sectionName}"]`)?.classList.add('active');

        // Update content
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(sectionName)?.classList.add('active');

        this.currentSection = sectionName;

        // Update section-specific content
        switch (sectionName) {
            case 'dashboard':
                this.updateDashboard();
                break;
            case 'products':
                this.updateProductsTable();
                break;
            case 'add-product':
                this.updateCategorySelects();
                break;
            case 'reports':
                setTimeout(() => chartManager.updateAllCharts(), 100);
                break;
            case 'settings':
                this.updateSettings();
                break;
        }
    }

    updateUI() {
        this.updateHeaderStats();
        this.updateCategoryFilters();
        if (this.currentSection === 'products') {
            this.updateProductsTable();
        }
    }

    updateHeaderStats() {
        const stats = inventory.getStatistics();
        
        document.getElementById('total-products').textContent = stats.totalProducts;
        document.getElementById('total-value').textContent = `$${stats.totalValue.toFixed(2)}`;
        document.getElementById('low-stock-count').textContent = stats.lowStockCount;
        
        // Update low stock card color
        const lowStockCard = document.getElementById('low-stock-card');
        if (stats.lowStockCount > 0) {
            lowStockCard.classList.add('low-stock');
        } else {
            lowStockCard.classList.remove('low-stock');
        }
    }

    updateDashboard() {
        const stats = inventory.getStatistics();
        
        // Quick stats
        document.getElementById('categories-count').textContent = stats.categoriesCount;
        document.getElementById('avg-price').textContent = `$${stats.avgPrice.toFixed(2)}`;
        document.getElementById('total-quantity').textContent = stats.totalQuantity;
        
        // Low stock list
        this.updateLowStockList();
        
        // Category chart
        chartManager.updateCategoryChart();
        
        // Recent activity
        this.updateRecentActivity();
    }

    updateLowStockList() {
        const lowStockProducts = inventory.getLowStockProducts();
        const container = document.getElementById('low-stock-list');
        
        if (lowStockProducts.length === 0) {
            container.innerHTML = '<p style="color: #27ae60; text-align: center;">All products are well stocked!</p>';
            return;
        }
        
        container.innerHTML = lowStockProducts.map(product => `
            <div class="low-stock-item">
                <div>
                    <strong>${product.name}</strong><br>
                    <small>${product.barcode}</small>
                </div>
                <div style="color: #e74c3c; font-weight: bold;">${product.quantity} left</div>
            </div>
        `).join('');
    }

    updateRecentActivity() {
        const activities = inventory.getRecentActivity(5);
        const container = document.getElementById('recent-activity');
        
        if (activities.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #6c757d;">No recent activity</p>';
            return;
        }
        
        container.innerHTML = activities.map(activity => `
            <div style="padding: 0.5rem; border-bottom: 1px solid #dee2e6;">
                <div>${activity.message}</div>
                <small style="color: #6c757d;">${new Date(activity.timestamp).toLocaleString()}</small>
            </div>
        `).join('');
    }

    updateCategoryFilters() {
        const categories = inventory.getCategories();
        const categoryFilter = document.getElementById('category-filter');
        
        if (categoryFilter) {
            const currentValue = categoryFilter.value;
            categoryFilter.innerHTML = '<option value="">All Categories</option>';
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category;
                option.textContent = category;
                categoryFilter.appendChild(option);
            });
            categoryFilter.value = currentValue;
        }
    }

    updateCategorySelects() {
        const categories = inventory.getCategories();
        const selects = ['product-category', 'modal-category'];
        
        selects.forEach(selectId => {
            const select = document.getElementById(selectId);
            if (select) {
                const currentValue = select.value;
                select.innerHTML = '<option value="">Select Category</option>';
                categories.forEach(category => {
                    const option = document.createElement('option');
                    option.value = category;
                    option.textContent = category;
                    select.appendChild(option);
                });
                select.value = currentValue;
            }
        });
    }

    applyFilters() {
        const searchTerm = document.getElementById('search-input')?.value || '';
        const category = document.getElementById('category-filter')?.value || '';
        const minQuantity = document.getElementById('min-quantity')?.value;
        const maxQuantity = document.getElementById('max-quantity')?.value;
        
        let products = inventory.products;
        
        // Apply search
        if (searchTerm) {
            products = inventory.searchProducts(searchTerm);
        }
        
        // Apply filters
        const filters = {};
        if (category) filters.category = category;
        if (minQuantity) filters.minQuantity = parseInt(minQuantity);
        if (maxQuantity) filters.maxQuantity = parseInt(maxQuantity);
        
        if (Object.keys(filters).length > 0) {
            products = products.filter(product => {
                if (filters.category && product.category !== filters.category) return false;
                if (filters.minQuantity !== undefined && product.quantity < filters.minQuantity) return false;
                if (filters.maxQuantity !== undefined && product.quantity > filters.maxQuantity) return false;
                return true;
            });
        }
        
        this.filteredProducts = products;
        this.updateProductsTable();
    }

    updateProductsTable() {
        const products = this.filteredProducts.length > 0 ? this.filteredProducts : inventory.products;
        const tbody = document.getElementById('products-tbody');
        
        if (!tbody) return;
        
        tbody.innerHTML = products.map(product => `
            <tr class="${product.quantity <= inventory.lowStockThreshold ? 'low-stock' : ''}">
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.barcode}</td>
                <td>$${product.price.toFixed(2)}</td>
                <td>${product.quantity}</td>
                <td>${product.category}</td>
                <td>$${(product.price * product.quantity).toFixed(2)}</td>
                <td>
                    <button class="btn btn-small btn-primary" onclick="app.editProduct(${product.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-small btn-danger" onclick="app.deleteProduct(${product.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    }

    handleProductSubmit() {
        const formData = {
            name: document.getElementById('product-name').value,
            barcode: document.getElementById('product-barcode').value,
            price: document.getElementById('product-price').value,
            quantity: document.getElementById('product-quantity').value,
            category: document.getElementById('product-category').value || document.getElementById('new-category').value,
            description: document.getElementById('product-description').value
        };
        
        const result = inventory.addProduct(formData);
        
        if (result.success) {
            this.showToast('Product added successfully!', 'success');
            document.getElementById('product-form').reset();
            this.updateUI();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    editProduct(id) {
        const product = inventory.findById(id);
        if (!product) return;
        
        // Populate modal
        document.getElementById('modal-product-id').value = product.id;
        document.getElementById('modal-name').value = product.name;
        document.getElementById('modal-barcode').value = product.barcode;
        document.getElementById('modal-price').value = product.price;
        document.getElementById('modal-quantity').value = product.quantity;
        
        this.updateCategorySelects();
        document.getElementById('modal-category').value = product.category;
        
        document.getElementById('modal-title').textContent = 'Edit Product';
        this.showModal();
    }

    deleteProduct(id) {
        const product = inventory.findById(id);
        if (!product) return;
        
        if (confirm(`Are you sure you want to delete "${product.name}"?`)) {
            const result = inventory.deleteProduct(id);
            if (result.success) {
                this.showToast('Product deleted successfully!', 'success');
                this.updateUI();
            } else {
                this.showToast(result.message, 'error');
            }
        }
    }

    saveProduct() {
        const id = document.getElementById('modal-product-id').value;
        const formData = {
            name: document.getElementById('modal-name').value,
            barcode: document.getElementById('modal-barcode').value,
            price: document.getElementById('modal-price').value,
            quantity: document.getElementById('modal-quantity').value,
            category: document.getElementById('modal-category').value
        };
        
        const result = id ? 
            inventory.updateProduct(parseInt(id), formData) : 
            inventory.addProduct(formData);
        
        if (result.success) {
            this.showToast(`Product ${id ? 'updated' : 'added'} successfully!`, 'success');
            this.closeModal();
            this.updateUI();
        } else {
            this.showToast(result.message, 'error');
        }
    }

    showModal() {
        document.getElementById('product-modal').style.display = 'block';
    }

    closeModal() {
        document.getElementById('product-modal').style.display = 'none';
        document.getElementById('modal-product-form').reset();
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        document.getElementById('toast-container').appendChild(toast);
        
        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

    updateSettings() {
        document.getElementById('low-stock-threshold').value = inventory.lowStockThreshold;
    }
}

// Global functions
function showAddProductModal() {
    app.updateCategorySelects();
    document.getElementById('modal-title').textContent = 'Add Product';
    document.getElementById('modal-product-id').value = '';
    app.showModal();
}

function closeModal() {
    app.closeModal();
}

function saveProduct() {
    app.saveProduct();
}

function clearFilters() {
    document.getElementById('search-input').value = '';
    document.getElementById('category-filter').value = '';
    document.getElementById('min-quantity').value = '';
    document.getElementById('max-quantity').value = '';
    app.applyFilters();
}

function sortTable(column) {
    // Implementation for table sorting
    app.showToast('Sorting functionality coming soon!', 'info');
}

function generateBarcode() {
    document.getElementById('product-barcode').value = inventory.generateBarcode();
}

function toggleCategoryInput() {
    const select = document.getElementById('product-category');
    const input = document.getElementById('new-category');
    const button = event.target;
    
    if (input.style.display === 'none') {
        input.style.display = 'block';
        select.style.display = 'none';
        button.innerHTML = '<i class="fas fa-list"></i> Select Category';
        input.focus();
    } else {
        input.style.display = 'none';
        select.style.display = 'block';
        button.innerHTML = '<i class="fas fa-plus"></i> New Category';
    }
}

function exportProducts() {
    const data = inventory.exportData();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `inventory-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
    app.showToast('Data exported successfully!', 'success');
}

function exportAllData() {
    exportProducts();
}

function importData() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.json';
    input.onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const data = JSON.parse(e.target.result);
                    const result = inventory.importData(data);
                    if (result.success) {
                        app.showToast('Data imported successfully!', 'success');
                        app.updateUI();
                    } else {
                        app.showToast(result.message, 'error');
                    }
                } catch (error) {
                    app.showToast('Invalid file format!', 'error');
                }
            };
            reader.readAsText(file);
        }
    };
    input.click();
}

function clearAllData() {
    if (confirm('Are you sure you want to clear all data? This action cannot be undone.')) {
        inventory.clearAllData();
        app.showToast('All data cleared!', 'success');
        app.updateUI();
    }
}

// Initialize app
const app = new InventoryApp();