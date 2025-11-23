import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GradeCalculator extends JDialog {
    private JTextField assignmentField, examField, participationField;
    private JTextField assignmentWeightField, examWeightField, participationWeightField;
    private JLabel resultLabel, gradeLabel;
    private ArrayList<Double> grades;
    private ArrayList<Double> weights;
    
    public GradeCalculator(JFrame parent) {
        super(parent, "Grade Calculator", true);
        grades = new ArrayList<>();
        weights = new ArrayList<>();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Grade Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(titleLabel, gbc);
        
        // Headers
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Component"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel("Grade"), gbc);
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Weight (%)"), gbc);
        
        // Assignment row
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Assignments:"), gbc);
        gbc.gridx = 1;
        assignmentField = new JTextField(10);
        mainPanel.add(assignmentField, gbc);
        gbc.gridx = 2;
        assignmentWeightField = new JTextField("40", 10);
        mainPanel.add(assignmentWeightField, gbc);
        
        // Exam row
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Exams:"), gbc);
        gbc.gridx = 1;
        examField = new JTextField(10);
        mainPanel.add(examField, gbc);
        gbc.gridx = 2;
        examWeightField = new JTextField("50", 10);
        mainPanel.add(examWeightField, gbc);
        
        // Participation row
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Participation:"), gbc);
        gbc.gridx = 1;
        participationField = new JTextField(10);
        mainPanel.add(participationField, gbc);
        gbc.gridx = 2;
        participationWeightField = new JTextField("10", 10);
        mainPanel.add(participationWeightField, gbc);
        
        // Calculate button
        JButton calculateBtn = new JButton("Calculate Final Grade");
        calculateBtn.addActionListener(e -> calculateGrade());
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        mainPanel.add(calculateBtn, gbc);
        
        // Result labels
        resultLabel = new JLabel("Final Grade: --");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 6;
        mainPanel.add(resultLabel, gbc);
        
        gradeLabel = new JLabel("Letter Grade: --");
        gradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 7;
        mainPanel.add(gradeLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        JButton clearBtn = new JButton("Clear");
        
        closeBtn.addActionListener(e -> dispose());
        clearBtn.addActionListener(e -> clearFields());
        
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
    
    private void calculateGrade() {
        try {
            double assignmentGrade = Double.parseDouble(assignmentField.getText().trim());
            double examGrade = Double.parseDouble(examField.getText().trim());
            double participationGrade = Double.parseDouble(participationField.getText().trim());
            
            double assignmentWeight = Double.parseDouble(assignmentWeightField.getText().trim());
            double examWeight = Double.parseDouble(examWeightField.getText().trim());
            double participationWeight = Double.parseDouble(participationWeightField.getText().trim());
            
            // Validate grades
            if (assignmentGrade < 0 || assignmentGrade > 100 ||
                examGrade < 0 || examGrade > 100 ||
                participationGrade < 0 || participationGrade > 100) {
                JOptionPane.showMessageDialog(this, "Grades must be between 0 and 100!");
                return;
            }
            
            // Validate weights
            double totalWeight = assignmentWeight + examWeight + participationWeight;
            if (Math.abs(totalWeight - 100) > 0.01) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Weights must sum to 100%% (Current: %.1f%%)", totalWeight));
                return;
            }
            
            // Calculate weighted average
            double finalGrade = (assignmentGrade * assignmentWeight / 100) +
                              (examGrade * examWeight / 100) +
                              (participationGrade * participationWeight / 100);
            
            String letterGrade = getLetterGrade(finalGrade);
            
            resultLabel.setText(String.format("Final Grade: %.2f", finalGrade));
            gradeLabel.setText("Letter Grade: " + letterGrade);
            
            // Color code the result
            Color gradeColor = getGradeColor(finalGrade);
            resultLabel.setForeground(gradeColor);
            gradeLabel.setForeground(gradeColor);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields!");
        }
    }
    
    private String getLetterGrade(double grade) {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else return "F";
    }
    
    private Color getGradeColor(double grade) {
        if (grade >= 90) return new Color(0, 128, 0); // Green
        else if (grade >= 80) return new Color(0, 100, 200); // Blue
        else if (grade >= 70) return new Color(255, 165, 0); // Orange
        else if (grade >= 60) return new Color(255, 140, 0); // Dark Orange
        else return Color.RED; // Red
    }
    
    private void clearFields() {
        assignmentField.setText("");
        examField.setText("");
        participationField.setText("");
        assignmentWeightField.setText("40");
        examWeightField.setText("50");
        participationWeightField.setText("10");
        resultLabel.setText("Final Grade: --");
        gradeLabel.setText("Letter Grade: --");
        resultLabel.setForeground(Color.BLACK);
        gradeLabel.setForeground(Color.BLACK);
    }
}