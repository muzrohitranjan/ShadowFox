public class Contact {
    private String name;
    private String phone;
    private String email;
    
    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
    
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return String.format("Name: %s | Phone: %s | Email: %s", name, phone, email);
    }
    
    public boolean contains(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase()) ||
               phone.contains(keyword) ||
               email.toLowerCase().contains(keyword.toLowerCase());
    }
}