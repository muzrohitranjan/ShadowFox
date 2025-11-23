import java.util.Scanner;
import java.util.InputMismatchException;

public class EnhancedCalculator {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== Enhanced Console Calculator ===");
        
        while (true) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1: basicArithmetic(); break;
                    case 2: scientificCalculations(); break;
                    case 3: unitConversions(); break;
                    case 4: 
                        System.out.println("Thank you for using the calculator!");
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
    
    private static void displayMenu() {
        System.out.println("\n--- Calculator Menu ---");
        System.out.println("1. Basic Arithmetic");
        System.out.println("2. Scientific Calculations");
        System.out.println("3. Unit Conversions");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }
    
    private static void basicArithmetic() {
        System.out.println("\n--- Basic Arithmetic ---");
        System.out.println("1. Addition  2. Subtraction  3. Multiplication  4. Division");
        System.out.print("Choose operation: ");
        
        try {
            int op = scanner.nextInt();
            System.out.print("Enter first number: ");
            double num1 = scanner.nextDouble();
            System.out.print("Enter second number: ");
            double num2 = scanner.nextDouble();
            
            double result = 0;
            switch (op) {
                case 1: result = num1 + num2; break;
                case 2: result = num1 - num2; break;
                case 3: result = num1 * num2; break;
                case 4: 
                    if (num2 == 0) {
                        System.out.println("Error: Division by zero!");
                        return;
                    }
                    result = num1 / num2;
                    break;
                default: 
                    System.out.println("Invalid operation!");
                    return;
            }
            System.out.println("Result: " + result);
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid number format!");
            scanner.nextLine();
        }
    }
    
    private static void scientificCalculations() {
        System.out.println("\n--- Scientific Calculations ---");
        System.out.println("1. Square Root  2. Power  3. Sine  4. Cosine  5. Logarithm");
        System.out.print("Choose operation: ");
        
        try {
            int op = scanner.nextInt();
            double result = 0;
            
            switch (op) {
                case 1:
                    System.out.print("Enter number: ");
                    double num = scanner.nextDouble();
                    if (num < 0) {
                        System.out.println("Error: Cannot calculate square root of negative number!");
                        return;
                    }
                    result = Math.sqrt(num);
                    break;
                case 2:
                    System.out.print("Enter base: ");
                    double base = scanner.nextDouble();
                    System.out.print("Enter exponent: ");
                    double exp = scanner.nextDouble();
                    result = Math.pow(base, exp);
                    break;
                case 3:
                    System.out.print("Enter angle in degrees: ");
                    double angle = scanner.nextDouble();
                    result = Math.sin(Math.toRadians(angle));
                    break;
                case 4:
                    System.out.print("Enter angle in degrees: ");
                    angle = scanner.nextDouble();
                    result = Math.cos(Math.toRadians(angle));
                    break;
                case 5:
                    System.out.print("Enter number: ");
                    num = scanner.nextDouble();
                    if (num <= 0) {
                        System.out.println("Error: Logarithm undefined for non-positive numbers!");
                        return;
                    }
                    result = Math.log10(num);
                    break;
                default:
                    System.out.println("Invalid operation!");
                    return;
            }
            System.out.println("Result: " + result);
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid number format!");
            scanner.nextLine();
        }
    }
    
    private static void unitConversions() {
        System.out.println("\n--- Unit Conversions ---");
        System.out.println("1. Temperature  2. Length  3. Weight");
        System.out.print("Choose conversion type: ");
        
        try {
            int type = scanner.nextInt();
            
            switch (type) {
                case 1: temperatureConversion(); break;
                case 2: lengthConversion(); break;
                case 3: weightConversion(); break;
                default: System.out.println("Invalid conversion type!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input!");
            scanner.nextLine();
        }
    }
    
    private static void temperatureConversion() {
        System.out.println("1. Celsius to Fahrenheit  2. Fahrenheit to Celsius  3. Celsius to Kelvin");
        System.out.print("Choose: ");
        
        try {
            int choice = scanner.nextInt();
            System.out.print("Enter temperature: ");
            double temp = scanner.nextDouble();
            double result = 0;
            
            switch (choice) {
                case 1: result = (temp * 9/5) + 32; System.out.println(temp + "°C = " + result + "°F"); break;
                case 2: result = (temp - 32) * 5/9; System.out.println(temp + "°F = " + result + "°C"); break;
                case 3: result = temp + 273.15; System.out.println(temp + "°C = " + result + "K"); break;
                default: System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input!");
            scanner.nextLine();
        }
    }
    
    private static void lengthConversion() {
        System.out.println("1. Meters to Feet  2. Feet to Meters  3. Kilometers to Miles");
        System.out.print("Choose: ");
        
        try {
            int choice = scanner.nextInt();
            System.out.print("Enter length: ");
            double length = scanner.nextDouble();
            double result = 0;
            
            switch (choice) {
                case 1: result = length * 3.28084; System.out.println(length + "m = " + result + "ft"); break;
                case 2: result = length / 3.28084; System.out.println(length + "ft = " + result + "m"); break;
                case 3: result = length * 0.621371; System.out.println(length + "km = " + result + "miles"); break;
                default: System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input!");
            scanner.nextLine();
        }
    }
    
    private static void weightConversion() {
        System.out.println("1. Kilograms to Pounds  2. Pounds to Kilograms  3. Grams to Ounces");
        System.out.print("Choose: ");
        
        try {
            int choice = scanner.nextInt();
            System.out.print("Enter weight: ");
            double weight = scanner.nextDouble();
            double result = 0;
            
            switch (choice) {
                case 1: result = weight * 2.20462; System.out.println(weight + "kg = " + result + "lbs"); break;
                case 2: result = weight / 2.20462; System.out.println(weight + "lbs = " + result + "kg"); break;
                case 3: result = weight * 0.035274; System.out.println(weight + "g = " + result + "oz"); break;
                default: System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input!");
            scanner.nextLine();
        }
    }
}