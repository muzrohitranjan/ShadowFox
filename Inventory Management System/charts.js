// Charts and Visualization
class ChartManager {
    constructor() {
        this.charts = {};
    }

    createCategoryValueChart() {
        const ctx = document.getElementById('category-value-chart');
        if (!ctx) return;

        const categoryAnalysis = inventory.getCategoryAnalysis();
        const categories = Object.keys(categoryAnalysis);
        const values = categories.map(cat => categoryAnalysis[cat].totalValue);
        
        if (this.charts.categoryValue) {
            this.charts.categoryValue.destroy();
        }

        this.charts.categoryValue = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: categories,
                datasets: [{
                    data: values,
                    backgroundColor: [
                        '#3498db', '#e74c3c', '#2ecc71', '#f39c12',
                        '#9b59b6', '#1abc9c', '#34495e', '#e67e22'
                    ],
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed;
                                return `${context.label}: $${value.toFixed(2)}`;
                            }
                        }
                    }
                }
            }
        });
    }

    createStockDistributionChart() {
        const ctx = document.getElementById('stock-distribution-chart');
        if (!ctx) return;

        const products = inventory.products;
        const stockRanges = {
            'Out of Stock (0)': products.filter(p => p.quantity === 0).length,
            'Low Stock (1-10)': products.filter(p => p.quantity > 0 && p.quantity <= 10).length,
            'Medium Stock (11-50)': products.filter(p => p.quantity > 10 && p.quantity <= 50).length,
            'High Stock (50+)': products.filter(p => p.quantity > 50).length
        };

        if (this.charts.stockDistribution) {
            this.charts.stockDistribution.destroy();
        }

        this.charts.stockDistribution = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(stockRanges),
                datasets: [{
                    label: 'Number of Products',
                    data: Object.values(stockRanges),
                    backgroundColor: ['#e74c3c', '#f39c12', '#3498db', '#2ecc71'],
                    borderColor: ['#c0392b', '#e67e22', '#2980b9', '#27ae60'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    updateCategoryChart() {
        const categoryAnalysis = inventory.getCategoryAnalysis();
        const categoryChart = document.getElementById('category-chart');
        
        if (!categoryChart) return;

        const maxValue = Math.max(...Object.values(categoryAnalysis).map(cat => cat.totalValue));
        
        categoryChart.innerHTML = '';
        
        Object.entries(categoryAnalysis).forEach(([category, data]) => {
            const percentage = maxValue > 0 ? (data.totalValue / maxValue) * 100 : 0;
            
            const categoryBar = document.createElement('div');
            categoryBar.className = 'category-bar';
            categoryBar.innerHTML = `
                <div class="category-name">${category}</div>
                <div class="category-progress">
                    <div class="category-fill" style="width: ${percentage}%"></div>
                </div>
                <div class="category-value">$${data.totalValue.toFixed(2)}</div>
            `;
            
            categoryChart.appendChild(categoryBar);
        });
    }

    updatePriceAnalysis() {
        const priceAnalysis = inventory.getPriceAnalysis();
        const container = document.getElementById('price-analysis');
        
        if (!container || !priceAnalysis.minPrice) return;

        container.innerHTML = `
            <div class="analysis-item">
                <span>Lowest Price:</span>
                <span>$${priceAnalysis.minPrice.toFixed(2)}</span>
            </div>
            <div class="analysis-item">
                <span>Highest Price:</span>
                <span>$${priceAnalysis.maxPrice.toFixed(2)}</span>
            </div>
            <div class="analysis-item">
                <span>Average Price:</span>
                <span>$${priceAnalysis.avgPrice.toFixed(2)}</span>
            </div>
            ${Object.entries(priceAnalysis.priceRanges).map(([range, count]) => `
                <div class="analysis-item">
                    <span>${range}:</span>
                    <span>${count} products</span>
                </div>
            `).join('')}
        `;
    }

    updateCategorySummary() {
        const categoryAnalysis = inventory.getCategoryAnalysis();
        const container = document.getElementById('category-summary');
        
        if (!container) return;

        container.innerHTML = Object.entries(categoryAnalysis).map(([category, data]) => `
            <div class="summary-item">
                <div>
                    <strong>${category}</strong><br>
                    <small>${data.count} products, ${data.totalQuantity} units</small>
                </div>
                <div>$${data.totalValue.toFixed(2)}</div>
            </div>
        `).join('');
    }

    updateAllCharts() {
        this.createCategoryValueChart();
        this.createStockDistributionChart();
        this.updateCategoryChart();
        this.updatePriceAnalysis();
        this.updateCategorySummary();
    }
}

const chartManager = new ChartManager();