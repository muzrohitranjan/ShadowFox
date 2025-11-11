import java.util.*;
import java.io.*;

public class ContactManager {
    private ArrayList<Contact> contacts;
    private Scanner scanner;
    
    public ContactManager() {
        contacts = new ArrayList<>();
        scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        ContactManager manager = new ContactManager();
        manager.run();
    }
    
    public void run() {
        System.out.println("=== Contact Management System ===");
        
        while (true) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1: addContact(); break;
                    case 2: viewContacts(); break;
                    case 3: updateContact(); break;
                    case 4: deleteContact(); break;
                    case 5: searchContacts(); break;
                    case 6: exportToFile(); break;
                    case 7: 
                        System.out.println("Thank you for using Contact Manager!");
                        return;
                    default: 
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- Contact Manager Menu ---");
        System.out.println("1. Add Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Update Contact");
        System.out.println("4. Delete Contact");
        System.out.println("5. Search Contacts");
        System.out.println("6. Export to File");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void addContact() {
        System.out.println("\n--- Add New Contact ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty!");
            return;
        }
        
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        // Check for duplicate phone numbers
        if (isDuplicatePhone(phone)) {
            System.out.println("Contact with this phone number already exists!");
            return;
        }
        
        Contact contact = new Contact(name, phone, email);
        contacts.add(contact);
        System.out.println("Contact added successfully!");
    }
    
    private void viewContacts() {
        System.out.println("\n--- All Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }
        
        // Sort contacts by name
        contacts.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " + contacts.get(i));
        }
    }
    
    private void updateContact() {
        System.out.println("\n--- Update Contact ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to update.");
            return;
        }
        
        viewContacts();
        System.out.print("Enter contact number to update: ");
        
        try {
            int index = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (index < 0 || index >= contacts.size()) {
                System.out.println("Invalid contact number!");
                return;
            }
            
            Contact contact = contacts.get(index);
            System.out.println("Current: " + contact);
            
            System.out.print("Enter new name (or press Enter to keep current): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) contact.setName(name);
            
            System.out.print("Enter new phone (or press Enter to keep current): ");
            String phone = scanner.nextLine().trim();
            if (!phone.isEmpty()) {
                if (isDuplicatePhone(phone) && !phone.equals(contact.getPhone())) {
                    System.out.println("Phone number already exists!");
                    return;
                }
                contact.setPhone(phone);
            }
            
            System.out.print("Enter new email (or press Enter to keep current): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) contact.setEmail(email);
            
            System.out.println("Contact updated successfully!");
            
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }
    
    private void deleteContact() {
        System.out.println("\n--- Delete Contact ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to delete.");
            return;
        }
        
        viewContacts();
        System.out.print("Enter contact number to delete: ");
        
        try {
            int index = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (index < 0 || index >= contacts.size()) {
                System.out.println("Invalid contact number!");
                return;
            }
            
            Contact contact = contacts.get(index);
            System.out.print("Are you sure you want to delete " + contact.getName() + "? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                contacts.remove(index);
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("Delete cancelled.");
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }
    
    private void searchContacts() {
        System.out.println("\n--- Search Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to search.");
            return;
        }
        
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty!");
            return;
        }
        
        ArrayList<Contact> results = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.contains(keyword)) {
                results.add(contact);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("No contacts found matching '" + keyword + "'");
        } else {
            System.out.println("Found " + results.size() + " contact(s):");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }
    
    private void exportToFile() {
        System.out.println("\n--- Export Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to export.");
            return;
        }
        
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("contacts.txt"));
            writer.println("=== Contact List ===");
            writer.println("Total Contacts: " + contacts.size());
            writer.println();
            
            for (int i = 0; i < contacts.size(); i++) {
                writer.println((i + 1) + ". " + contacts.get(i));
            }
            
            writer.close();
            System.out.println("Contacts exported to 'contacts.txt' successfully!");
            
        } catch (IOException e) {
            System.out.println("Error exporting contacts: " + e.getMessage());
        }
    }
    
    private boolean isDuplicatePhone(String phone) {
        if (phone.isEmpty()) return false;
        
        for (Contact contact : contacts) {
            if (contact.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }
}