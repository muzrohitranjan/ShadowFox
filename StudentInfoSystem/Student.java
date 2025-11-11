public class Student {
    private int id;
    private String name;
    private String email;
    private String course;
    private double grade;
    private String phone;
    
    public Student(int id, String name, String email, String course, double grade, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.grade = grade;
        this.phone = phone;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public double getGrade() { return grade; }
    public String getPhone() { return phone; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }
    public void setGrade(double grade) { this.grade = grade; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getGradeLevel() {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else return "F";
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | Grade: %.1f (%s)", 
                           id, name, course, grade, getGradeLevel());
    }
}