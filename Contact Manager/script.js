let contacts = [];
let editingIndex = -1;
let currentView = 'grid';
let currentSort = 'name';
let currentFilter = 'all';

// Load contacts from localStorage on page load
document.addEventListener('DOMContentLoaded', function() {
    loadContacts();
    displayContacts();
    updateStats();
});

// Add contact form handler
document.getElementById('contactForm').addEventListener('submit', function(e) {
    e.preventDefault();
    addContact();
});

// Edit contact form handler
document.getElementById('editForm').addEventListener('submit', function(e) {
    e.preventDefault();
    updateContact();
});

function addContact() {
    const name = document.getElementById('name').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const email = document.getElementById('email').value.trim();
    const company = document.getElementById('company').value.trim();
    const notes = document.getElementById('notes').value.trim();
    
    if (!name || !phone || !email) {
        alert('Please fill required fields (Name, Phone, Email)');
        return;
    }
    
    // Check for duplicate phone
    if (isDuplicatePhone(phone)) {
        alert('Contact with this phone number already exists!');
        return;
    }
    
    const contact = {
        id: Date.now(),
        name: name,
        phone: phone,
        email: email,
        company: company,
        notes: notes,
        favorite: false,
        dateAdded: new Date().toISOString().split('T')[0]
    };
    
    contacts.push(contact);
    saveContacts();
    displayContacts();
    updateStats();
    clearForm();
    
    showNotification('Contact added successfully!', 'success');
}

function displayContacts() {
    const contactsList = document.getElementById('contactsList');
    const contactCount = document.getElementById('contactCount');
    
    let filteredContacts = getFilteredContacts();
    contactCount.textContent = filteredContacts.length;
    
    if (filteredContacts.length === 0) {
        contactsList.innerHTML = '<div class="empty-state">No contacts found. Add your first contact above!</div>';
        return;
    }
    
    // Apply sorting
    filteredContacts = getSortedContacts(filteredContacts);
    
    contactsList.className = currentView === 'grid' ? 'grid-view' : 'list-view';
    
    contactsList.innerHTML = filteredContacts.map(contact => `
        <div class="contact-item">
            <button class="favorite-btn ${contact.favorite ? 'active' : ''}" onclick="toggleFavorite(${contact.id})">
                ${contact.favorite ? '❤️' : '🤍'}
            </button>
            <div class="contact-info">
                <div class="contact-name">${contact.name}</div>
                <div class="contact-details">📞 ${contact.phone} | ✉️ ${contact.email}</div>
                ${contact.company ? `<div class="contact-company">🏢 ${contact.company}</div>` : ''}
                ${contact.notes ? `<div class="contact-notes">📝 ${contact.notes}</div>` : ''}
            </div>
            <div class="contact-actions">
                <button class="edit-btn" onclick="editContact(${contact.id})">✏️ Edit</button>
                <button class="delete-btn" onclick="deleteContact(${contact.id})">🗑️ Delete</button>
            </div>
        </div>
    `).join('');
}

function editContact(id) {
    const contact = contacts.find(c => c.id === id);
    if (!contact) return;
    
    editingIndex = contacts.findIndex(c => c.id === id);
    
    document.getElementById('editName').value = contact.name;
    document.getElementById('editPhone').value = contact.phone;
    document.getElementById('editEmail').value = contact.email;
    document.getElementById('editCompany').value = contact.company || '';
    document.getElementById('editNotes').value = contact.notes || '';
    
    document.getElementById('editModal').style.display = 'block';
}

function updateContact() {
    if (editingIndex === -1) return;
    
    const name = document.getElementById('editName').value.trim();
    const phone = document.getElementById('editPhone').value.trim();
    const email = document.getElementById('editEmail').value.trim();
    const company = document.getElementById('editCompany').value.trim();
    const notes = document.getElementById('editNotes').value.trim();
    
    if (!name || !phone || !email) {
        alert('Please fill required fields (Name, Phone, Email)');
        return;
    }
    
    // Check for duplicate phone (excluding current contact)
    const currentContact = contacts[editingIndex];
    if (phone !== currentContact.phone && isDuplicatePhone(phone)) {
        alert('Contact with this phone number already exists!');
        return;
    }
    
    contacts[editingIndex] = {
        ...currentContact,
        name: name,
        phone: phone,
        email: email,
        company: company,
        notes: notes
    };
    
    saveContacts();
    displayContacts();
    updateStats();
    closeModal();
    
    showNotification('Contact updated successfully!', 'success');
}

function deleteContact(id) {
    const contact = contacts.find(c => c.id === id);
    if (!contact) return;
    
    if (confirm(`Are you sure you want to delete ${contact.name}?`)) {
        contacts = contacts.filter(c => c.id !== id);
        saveContacts();
        displayContacts();
        alert('Contact deleted successfully!');
    }
}

function searchContacts() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const contactsList = document.getElementById('contactsList');
    
    if (!searchTerm) {
        displayContacts();
        return;
    }
    
    const filteredContacts = contacts.filter(contact =>
        contact.name.toLowerCase().includes(searchTerm) ||
        contact.phone.includes(searchTerm) ||
        contact.email.toLowerCase().includes(searchTerm)
    );
    
    if (filteredContacts.length === 0) {
        contactsList.innerHTML = '<div class="empty-state">No contacts found matching your search.</div>';
        return;
    }
    
    contactsList.innerHTML = filteredContacts.map(contact => `
        <div class="contact-item">
            <div class="contact-info">
                <div class="contact-name">${contact.name}</div>
                <div class="contact-details">📞 ${contact.phone} | ✉️ ${contact.email}</div>
            </div>
            <div class="contact-actions">
                <button class="edit-btn" onclick="editContact(${contact.id})">✏️ Edit</button>
                <button class="delete-btn" onclick="deleteContact(${contact.id})">🗑️ Delete</button>
            </div>
        </div>
    `).join('');
}

function exportContacts(format = 'txt') {
    if (contacts.length === 0) {
        alert('No contacts to export!');
        return;
    }
    
    let exportData = '';
    let filename = '';
    let mimeType = '';
    
    if (format === 'csv') {
        exportData = 'Name,Phone,Email,Company,Notes,Favorite,Date Added\n';
        contacts.forEach(contact => {
            exportData += `"${contact.name}","${contact.phone}","${contact.email}","${contact.company || ''}","${contact.notes || ''}","${contact.favorite}","${contact.dateAdded}"\n`;
        });
        filename = 'contacts.csv';
        mimeType = 'text/csv';
    } else {
        exportData = '=== Contact List ===\n';
        exportData += `Total Contacts: ${contacts.length}\n\n`;
        
        contacts.sort((a, b) => a.name.localeCompare(b.name));
        
        contacts.forEach((contact, index) => {
            exportData += `${index + 1}. Name: ${contact.name}\n`;
            exportData += `   Phone: ${contact.phone}\n`;
            exportData += `   Email: ${contact.email}\n`;
            if (contact.company) exportData += `   Company: ${contact.company}\n`;
            if (contact.notes) exportData += `   Notes: ${contact.notes}\n`;
            exportData += `   Favorite: ${contact.favorite ? 'Yes' : 'No'}\n`;
            exportData += `   Added: ${contact.dateAdded}\n\n`;
        });
        filename = 'contacts.txt';
        mimeType = 'text/plain';
    }
    
    const blob = new Blob([exportData], { type: mimeType });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
    
    showNotification(`Contacts exported as ${format.toUpperCase()} successfully!`, 'success');
}

function clearAllContacts() {
    if (contacts.length === 0) {
        alert('No contacts to clear!');
        return;
    }
    
    if (confirm('Are you sure you want to delete ALL contacts? This cannot be undone!')) {
        contacts = [];
        saveContacts();
        displayContacts();
        alert('All contacts cleared!');
    }
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
    editingIndex = -1;
}

function clearForm() {
    document.getElementById('name').value = '';
    document.getElementById('phone').value = '';
    document.getElementById('email').value = '';
    document.getElementById('company').value = '';
    document.getElementById('notes').value = '';
}

function isDuplicatePhone(phone) {
    return contacts.some(contact => contact.phone === phone);
}

function saveContacts() {
    localStorage.setItem('contacts', JSON.stringify(contacts));
}

function loadContacts() {
    const savedContacts = localStorage.getItem('contacts');
    if (savedContacts) {
        contacts = JSON.parse(savedContacts);
    }
}

function importContacts() {
    document.getElementById('importFile').click();
}

function handleImport() {
    const file = document.getElementById('importFile').files[0];
    if (!file) return;
    
    const reader = new FileReader();
    reader.onload = function(e) {
        try {
            const content = e.target.result;
            let importedContacts = [];
            
            if (file.name.endsWith('.csv')) {
                const lines = content.split('\n');
                const headers = lines[0].split(',');
                
                for (let i = 1; i < lines.length; i++) {
                    if (lines[i].trim()) {
                        const values = lines[i].split(',').map(v => v.replace(/"/g, ''));
                        importedContacts.push({
                            id: Date.now() + i,
                            name: values[0] || '',
                            phone: values[1] || '',
                            email: values[2] || '',
                            company: values[3] || '',
                            notes: values[4] || '',
                            favorite: values[5] === 'true',
                            dateAdded: values[6] || new Date().toISOString().split('T')[0]
                        });
                    }
                }
            }
            
            if (importedContacts.length > 0) {
                contacts = [...contacts, ...importedContacts];
                saveContacts();
                displayContacts();
                updateStats();
                showNotification(`${importedContacts.length} contacts imported successfully!`, 'success');
            }
        } catch (error) {
            showNotification('Error importing contacts. Please check file format.', 'error');
        }
    };
    reader.readAsText(file);
}

function toggleFavorite(id) {
    const contact = contacts.find(c => c.id === id);
    if (contact) {
        contact.favorite = !contact.favorite;
        saveContacts();
        displayContacts();
        updateStats();
    }
}

function setView(view) {
    currentView = view;
    document.getElementById('gridView').classList.toggle('active', view === 'grid');
    document.getElementById('listView').classList.toggle('active', view === 'list');
    displayContacts();
}

function sortContacts() {
    currentSort = document.getElementById('sortBy').value;
    displayContacts();
}

function filterContacts() {
    currentFilter = document.getElementById('filterBy').value;
    displayContacts();
}

function getFilteredContacts() {
    let filtered = [...contacts];
    
    if (currentFilter === 'favorites') {
        filtered = filtered.filter(c => c.favorite);
    } else if (currentFilter === 'recent') {
        const today = new Date().toISOString().split('T')[0];
        filtered = filtered.filter(c => c.dateAdded === today);
    }
    
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    if (searchTerm) {
        filtered = filtered.filter(contact =>
            contact.name.toLowerCase().includes(searchTerm) ||
            contact.phone.includes(searchTerm) ||
            contact.email.toLowerCase().includes(searchTerm) ||
            (contact.company && contact.company.toLowerCase().includes(searchTerm))
        );
    }
    
    return filtered;
}

function getSortedContacts(contactsToSort) {
    return contactsToSort.sort((a, b) => {
        switch (currentSort) {
            case 'recent':
                return new Date(b.dateAdded) - new Date(a.dateAdded);
            case 'company':
                return (a.company || '').localeCompare(b.company || '');
            default:
                return a.name.localeCompare(b.name);
        }
    });
}

function updateStats() {
    const today = new Date().toISOString().split('T')[0];
    const recentCount = contacts.filter(c => c.dateAdded === today).length;
    const favoriteCount = contacts.filter(c => c.favorite).length;
    
    document.getElementById('totalContacts').textContent = contacts.length;
    document.getElementById('recentContacts').textContent = recentCount;
    document.getElementById('favoriteContacts').textContent = favoriteCount;
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: bold;
        z-index: 1000;
        animation: slideIn 0.3s ease;
        background: ${type === 'success' ? '#00b894' : type === 'error' ? '#e17055' : '#74b9ff'};
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Add CSS animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
`;
document.head.appendChild(style);