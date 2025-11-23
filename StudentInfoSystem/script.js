let students = [];
let nextId = 1;
let editingId = null;

document.addEventListener('DOMContentLoaded', function() {
    loadStudents();
    displayStudents();
    updateStats();
    
    document.getElementById('studentForm').addEventListener('submit', handleFormSubmit);
    document.getElementById('clearBtn').addEventListener('click', clearForm);
    document.getElementById('calculatorBtn').addEventListener('click', openCalculator);
});

function handleFormSubmit(e) {
    e.preventDefault();
    
    if (editingId) {
        updateStudent();
    } else {
        addStudent();
    }
}

function addStudent() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const course = document.getElementById('course').value.trim();
    const grade = parseFloat(document.getElementById('grade').value);
    const phone = document.getElementById('phone').value.trim();
    
    if (!validateInput(name, email, course, grade, phone)) return;
    
    if (isDuplicateEmail(email)) {
        showNotification('Student with this email already exists!', 'error');
        return;
    }
    
    const student = {
        id: nextId++,
        name: name,
        email: email,
        course: course,
        grade: grade,
        phone: phone,
        dateAdded: new Date().toISOString().split('T')[0]
    };
    
    students.push(student);
    saveStudents();
    displayStudents();
    updateStats();
    clearForm();
    
    showNotification('Student added successfully!', 'success');
}

function updateStudent() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const course = document.getElementById('course').value.trim();
    const grade = parseFloat(document.getElementById('grade').value);
    const phone = document.getElementById('phone').value.trim();
    
    if (!validateInput(name, email, course, grade, phone)) return;
    
    const student = students.find(s => s.id === editingId);
    if (student.email !== email && isDuplicateEmail(email)) {
        showNotification('Student with this email already exists!', 'error');
        return;
    }
    
    const studentIndex = students.findIndex(s => s.id === editingId);
    students[studentIndex] = {
        ...students[studentIndex],
        name: name,
        email: email,
        course: course,
        grade: grade,
        phone: phone
    };
    
    saveStudents();
    displayStudents();
    updateStats();
    clearForm();
    
    showNotification('Student updated successfully!', 'success');
}

function editStudent(id) {
    const student = students.find(s => s.id === id);
    if (!student) return;
    
    editingId = id;
    
    document.getElementById('studentId').value = student.id;
    document.getElementById('name').value = student.name;
    document.getElementById('email').value = student.email;
    document.getElementById('course').value = student.course;
    document.getElementById('grade').value = student.grade;
    document.getElementById('phone').value = student.phone;
    
    document.getElementById('addBtn').style.display = 'none';
    document.getElementById('updateBtn').style.display = 'inline-block';
    
    document.getElementById('name').focus();
}

function deleteStudent(id) {
    const student = students.find(s => s.id === id);
    if (!student) return;
    
    if (confirm(`Are you sure you want to delete ${student.name}?`)) {
        students = students.filter(s => s.id !== id);
        saveStudents();
        displayStudents();
        updateStats();
        
        showNotification('Student deleted successfully!', 'success');
    }
}

function clearForm() {
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    editingId = null;
    
    document.getElementById('addBtn').style.display = 'inline-block';
    document.getElementById('updateBtn').style.display = 'none';
}

function displayStudents() {
    const tbody = document.getElementById('studentTableBody');
    const recordCount = document.getElementById('recordCount');
    
    let filteredStudents = getFilteredStudents();
    recordCount.textContent = filteredStudents.length;
    
    if (filteredStudents.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No students found</td></tr>';
        return;
    }
    
    tbody.innerHTML = filteredStudents.map(student => `
        <tr>
            <td>${student.id}</td>
            <td>${student.name}</td>
            <td>${student.email}</td>
            <td>${student.course}</td>
            <td>${student.grade.toFixed(1)}</td>
            <td class="grade-${getLetterGrade(student.grade)}">${getLetterGrade(student.grade)}</td>
            <td>${student.phone}</td>
            <td>
                <button class="action-btn edit-btn" onclick="editStudent(${student.id})">Edit</button>
                <button class="action-btn delete-btn" onclick="deleteStudent(${student.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

function searchStudents() {
    displayStudents();
}

function filterStudents() {
    displayStudents();
}

function getFilteredStudents() {
    let filtered = [...students];
    
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    if (searchTerm) {
        filtered = filtered.filter(student =>
            student.name.toLowerCase().includes(searchTerm) ||
            student.email.toLowerCase().includes(searchTerm) ||
            student.course.toLowerCase().includes(searchTerm) ||
            student.phone.includes(searchTerm)
        );
    }
    
    const gradeFilter = document.getElementById('filterGrade').value;
    if (gradeFilter !== 'all') {
        filtered = filtered.filter(student => getLetterGrade(student.grade) === gradeFilter);
    }
    
    return filtered.sort((a, b) => a.name.localeCompare(b.name));
}

function getLetterGrade(grade) {
    if (grade >= 90) return 'A';
    else if (grade >= 80) return 'B';
    else if (grade >= 70) return 'C';
    else if (grade >= 60) return 'D';
    else return 'F';
}

function updateStats() {
    const totalStudents = students.length;
    const avgGrade = totalStudents > 0 ? 
        students.reduce((sum, s) => sum + s.grade, 0) / totalStudents : 0;
    const topPerformers = students.filter(s => s.grade >= 90).length;
    
    document.getElementById('totalStudents').textContent = totalStudents;
    document.getElementById('avgGrade').textContent = avgGrade.toFixed(1);
    document.getElementById('topPerformers').textContent = topPerformers;
}

function validateInput(name, email, course, grade, phone) {
    if (!name || !email || !course || isNaN(grade) || !phone) {
        showNotification('Please fill all fields correctly!', 'error');
        return false;
    }
    
    if (grade < 0 || grade > 100) {
        showNotification('Grade must be between 0 and 100!', 'error');
        return false;
    }
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showNotification('Please enter a valid email address!', 'error');
        return false;
    }
    
    return true;
}

function isDuplicateEmail(email) {
    return students.some(student => student.email === email);
}

function openCalculator() {
    document.getElementById('calculatorModal').style.display = 'block';
}

function closeCalculator() {
    document.getElementById('calculatorModal').style.display = 'none';
    document.getElementById('calculatorResult').innerHTML = '';
}

function calculateFinalGrade() {
    const assignmentGrade = parseFloat(document.getElementById('assignmentGrade').value) || 0;
    const examGrade = parseFloat(document.getElementById('examGrade').value) || 0;
    const participationGrade = parseFloat(document.getElementById('participationGrade').value) || 0;
    
    const assignmentWeight = parseFloat(document.getElementById('assignmentWeight').value) || 0;
    const examWeight = parseFloat(document.getElementById('examWeight').value) || 0;
    const participationWeight = parseFloat(document.getElementById('participationWeight').value) || 0;
    
    const totalWeight = assignmentWeight + examWeight + participationWeight;
    
    if (totalWeight !== 100) {
        showNotification(`Weights must sum to 100% (Current: ${totalWeight}%)`, 'error');
        return;
    }
    
    if (assignmentGrade > 100 || examGrade > 100 || participationGrade > 100) {
        showNotification('Grades cannot exceed 100!', 'error');
        return;
    }
    
    const finalGrade = (assignmentGrade * assignmentWeight / 100) +
                      (examGrade * examWeight / 100) +
                      (participationGrade * participationWeight / 100);
    
    const letterGrade = getLetterGrade(finalGrade);
    const gradeColor = getGradeColor(letterGrade);
    
    document.getElementById('calculatorResult').innerHTML = `
        <div style="color: ${gradeColor}">
            <div style="font-size: 24px; margin-bottom: 10px;">Final Grade: ${finalGrade.toFixed(2)}</div>
            <div style="font-size: 20px;">Letter Grade: ${letterGrade}</div>
        </div>
    `;
}

function getGradeColor(letterGrade) {
    const colors = {
        'A': '#00b894',
        'B': '#0984e3',
        'C': '#fdcb6e',
        'D': '#e17055',
        'F': '#d63031'
    };
    return colors[letterGrade] || '#636e72';
}

function exportStudents(format) {
    if (students.length === 0) {
        showNotification('No students to export!', 'error');
        return;
    }
    
    let exportData = '';
    let filename = '';
    let mimeType = '';
    
    if (format === 'csv') {
        exportData = 'ID,Name,Email,Course,Grade,Letter Grade,Phone,Date Added\\n';
        students.forEach(student => {
            exportData += `${student.id},"${student.name}","${student.email}","${student.course}",${student.grade},"${getLetterGrade(student.grade)}","${student.phone}","${student.dateAdded}"\\n`;
        });
        filename = 'students.csv';
        mimeType = 'text/csv';
    } else {
        exportData = '=== Student Information System Report ===\\n\\n';
        exportData += `Total Students: ${students.length}\\n`;
        exportData += `Average Grade: ${(students.reduce((sum, s) => sum + s.grade, 0) / students.length).toFixed(2)}\\n\\n`;
        
        students.forEach((student, index) => {
            exportData += `${index + 1}. ${student.name}\\n`;
            exportData += `   ID: ${student.id}\\n`;
            exportData += `   Email: ${student.email}\\n`;
            exportData += `   Course: ${student.course}\\n`;
            exportData += `   Grade: ${student.grade} (${getLetterGrade(student.grade)})\\n`;
            exportData += `   Phone: ${student.phone}\\n`;
            exportData += `   Date Added: ${student.dateAdded}\\n\\n`;
        });
        filename = 'students.txt';
        mimeType = 'text/plain';
    }
    
    const blob = new Blob([exportData], { type: mimeType });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
    
    showNotification(`Students exported as ${format.toUpperCase()} successfully!`, 'success');
}

function clearAllStudents() {
    if (students.length === 0) {
        showNotification('No students to clear!', 'error');
        return;
    }
    
    if (confirm('Are you sure you want to delete ALL students? This cannot be undone!')) {
        students = [];
        nextId = 1;
        saveStudents();
        displayStudents();
        updateStats();
        clearForm();
        
        showNotification('All students cleared!', 'success');
    }
}

function saveStudents() {
    localStorage.setItem('students', JSON.stringify(students));
    localStorage.setItem('nextId', nextId.toString());
}

function loadStudents() {
    const savedStudents = localStorage.getItem('students');
    const savedNextId = localStorage.getItem('nextId');
    
    if (savedStudents) {
        students = JSON.parse(savedStudents);
    }
    
    if (savedNextId) {
        nextId = parseInt(savedNextId);
    }
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
        background: ${type === 'success' ? '#00b894' : type === 'error' ? '#d63031' : '#0984e3'};
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('calculatorModal');
    if (event.target === modal) {
        closeCalculator();
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