const API_BASE = 'http://localhost:8080';
let authToken = localStorage.getItem('authToken');
let currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
let cart = JSON.parse(localStorage.getItem('cart') || '[]');

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    if (authToken) {
        showMainApp();
        loadProducts();
        loadOrders();
        updateCartDisplay();
    }
});

// Authentication
async function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showMainApp();
            loadProducts();
            loadOrders();
        } else {
            alert('Login failed');
        }
    } catch (error) {
        alert('Login error: ' + error.message);
    }
}

async function register() {
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName, lastName, email, password })
        });
        
        if (response.ok) {
            alert('Registration successful! Please login.');
            showLogin();
        } else {
            alert('Registration failed');
        }
    } catch (error) {
        alert('Registration error: ' + error.message);
    }
}

function logout() {
    authToken = null;
    currentUser = {};
    cart = [];
    localStorage.clear();
    showLogin();
}

// UI Navigation
function showLogin() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('mainApp').style.display = 'none';
    document.getElementById('loginBtn').style.display = 'inline';
    document.getElementById('logoutBtn').style.display = 'none';
    document.getElementById('userInfo').style.display = 'none';
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
}

function showMainApp() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('mainApp').style.display = 'block';
    document.getElementById('loginBtn').style.display = 'none';
    document.getElementById('logoutBtn').style.display = 'inline';
    document.getElementById('userInfo').style.display = 'inline';
    document.getElementById('userInfo').textContent = `Welcome, ${currentUser.email}`;
}

// Products
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error('Error loading products:', error);
    }
}

async function searchProducts() {
    const query = document.getElementById('searchInput').value;
    if (!query) return loadProducts();
    
    try {
        const response = await fetch(`${API_BASE}/products/search?name=${query}`);
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error('Error searching products:', error);
    }
}

function displayProducts(products) {
    const container = document.getElementById('productList');
    container.innerHTML = products.map(product => `
        <div class="product-card">
            <h3>${product.name}</h3>
            <p>${product.description}</p>
            <div class="price">$${product.price}</div>
            <button onclick="addToCart('${product.id}', '${product.name}', ${product.price})">
                Add to Cart
            </button>
        </div>
    `).join('');
}

// Cart Management
function addToCart(productId, productName, price) {
    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({ productId, productName, price, quantity: 1 });
    }
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartDisplay();
}

function removeFromCart(productId) {
    cart = cart.filter(item => item.productId !== productId);
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartDisplay();
}

function updateCartDisplay() {
    const container = document.getElementById('cartItems');
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
    container.innerHTML = cart.map(item => `
        <div class="cart-item">
            <span>${item.productName} x ${item.quantity}</span>
            <span>$${(item.price * item.quantity).toFixed(2)}</span>
            <button onclick="removeFromCart('${item.productId}')">Remove</button>
        </div>
    `).join('');
    
    document.getElementById('cartTotal').textContent = total.toFixed(2);
    document.getElementById('checkoutBtn').disabled = cart.length === 0;
}

// Checkout
async function checkout() {
    if (cart.length === 0) return;
    
    const orderData = {
        items: cart.map(item => ({
            productId: item.productId,
            quantity: item.quantity
        }))
    };
    
    try {
        const response = await fetch(`${API_BASE}/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(orderData)
        });
        
        if (response.ok) {
            const order = await response.json();
            alert(`Order placed successfully! Order ID: ${order.id}`);
            cart = [];
            localStorage.setItem('cart', JSON.stringify(cart));
            updateCartDisplay();
            loadOrders();
            
            // Simulate payment
            setTimeout(() => processPayment(order.id, order.totalAmount), 1000);
        } else {
            alert('Order failed');
        }
    } catch (error) {
        alert('Checkout error: ' + error.message);
    }
}

async function processPayment(orderId, amount) {
    const paymentData = {
        orderId: orderId,
        amount: amount,
        paymentMethod: 'CREDIT_CARD',
        cardNumber: '4111111111111111',
        expiryDate: '12/25',
        cvv: '123'
    };
    
    try {
        const response = await fetch(`${API_BASE}/payments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(paymentData)
        });
        
        if (response.ok) {
            const payment = await response.json();
            alert(`Payment ${payment.status.toLowerCase()}!`);
            loadOrders();
        }
    } catch (error) {
        console.error('Payment error:', error);
    }
}

// Orders
async function loadOrders() {
    if (!currentUser.userId) return;
    
    try {
        const response = await fetch(`${API_BASE}/orders/user/${currentUser.userId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        
        if (response.ok) {
            const orders = await response.json();
            displayOrders(orders);
        }
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

function displayOrders(orders) {
    const container = document.getElementById('orderList');
    if (orders.length === 0) {
        container.innerHTML = '<p>No orders found.</p>';
        return;
    }
    
    container.innerHTML = orders.map(order => `
        <div class="order-item">
            <h4>Order #${order.id}</h4>
            <p>Total: $${order.totalAmount}</p>
            <p>Status: <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span></p>
            <p>Date: ${new Date(order.createdAt).toLocaleDateString()}</p>
            <div>
                ${order.orderItems.map(item => 
                    `<div>${item.productName} x ${item.quantity} - $${item.price}</div>`
                ).join('')}
            </div>
        </div>
    `).join('');
}