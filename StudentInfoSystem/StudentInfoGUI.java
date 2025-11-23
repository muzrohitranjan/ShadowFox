import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StudentInfoGUI extends JFrame {
    private ArrayList<Student> students;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JTextField idField, nameField, emailField, courseField, gradeField, phoneField;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, calculateBtn;
    private JLabel avgGradeLabel, totalStudentsLabel;
    private int nextId = 1;
    
    public StudentInfoGUI() {
        students = new ArrayList<>();
        initializeGUI();
        updateStats();
    }
    
    private void initializeGUI() {
        setTitle("Student Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatsPanel(), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Input fields
        idField = new JTextField(10);
        idField.setEditable(false);
        nameField = new JTextField(15);
        emailField = new JTextField(15);
        courseField = new JTextField(15);
        gradeField = new JTextField(10);
        phoneField = new JTextField(15);
        
        // Labels and fields layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 3;
        panel.add(courseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Grade:"), gbc);
        gbc.gridx = 1;
        panel.add(gradeField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3;
        panel.add(phoneField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addBtn = new JButton("Add Student");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        calculateBtn = new JButton("Grade Calculator");
        
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        calculateBtn.addActionListener(e -> openGradeCalculator());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(calculateBtn);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Records"));
        
        String[] columns = {"ID", "Name", "Email", "Course", "Grade", "Grade Level", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    loadSelectedStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        totalStudentsLabel = new JLabel("Total Students: 0");
        avgGradeLabel = new JLabel("Average Grade: 0.0");
        
        panel.add(totalStudentsLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(avgGradeLabel);
        
        return panel;
    }
    
    private void addStudent() {
        if (!validateInput()) return;
        
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = courseField.getText().trim();
            double grade = Double.parseDouble(gradeField.getText().trim());
            String phone = phoneField.getText().trim();
            
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100!");
                return;
            }
            
            Student student = new Student(nextId++, name, email, course, grade, phone);
            students.add(student);
            
            addToTable(student);
            clearFields();
            updateStats();
            
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid grade!");
        }
    }
    
    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update!");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = courseField.getText().trim();
            double grade = Double.parseDouble(gradeField.getText().trim());
            String phone = phoneField.getText().trim();
            
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100!");
                return;
            }
            
            Student student = findStudentById(id);
            if (student != null) {
                student.setName(name);
                student.setEmail(email);
                student.setCourse(course);
                student.setGrade(grade);
                student.setPhone(phone);
                
                refreshTable();
                clearFields();
                updateStats();
                
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid grade!");
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this student?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            students.removeIf(s -> s.getId() == id);
            
            refreshTable();
            clearFields();
            updateStats();
            
            JOptionPane.showMessageDialog(this, "Student deleted successfully!");
        }
    }
    
    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            courseField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            gradeField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 6).toString());
        }
    }
    
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        courseField.setText("");
        gradeField.setText("");
        phoneField.setText("");
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            courseField.getText().trim().isEmpty() ||
            gradeField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return false;
        }
        return true;
    }
    
    private void addToTable(Student student) {
        Object[] row = {
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getCourse(),
            student.getGrade(),
            student.getGradeLevel(),
            student.getPhone()
        };
        tableModel.addRow(row);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student student : students) {
            addToTable(student);
        }
    }
    
    private Student findStudentById(int id) {
        return students.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }
    
    private void updateStats() {
        totalStudentsLabel.setText("Total Students: " + students.size());
        
        if (students.isEmpty()) {
            avgGradeLabel.setText("Average Grade: 0.0");
        } else {
            double avg = students.stream().mapToDouble(Student::getGrade).average().orElse(0.0);
            avgGradeLabel.setText(String.format("Average Grade: %.2f", avg));
        }
    }
    
    private void openGradeCalculator() {
        new GradeCalculator(this);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInfoGUI());
    }
}