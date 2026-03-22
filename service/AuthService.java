package service;

import file.CSVHandler;
import model.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {

    private final Map<String, String> adminCredentials;
    private final Map<String, Employee> employeeRegistry;

    public AuthService() {
        adminCredentials = new HashMap<>();
        adminCredentials.put("admin", "admin123");

        employeeRegistry = new HashMap<>();
        loadEmployees();
    }

    private void loadEmployees() {
        List<Employee> loaded = CSVHandler.loadEmployees();

        if (loaded.isEmpty()) {

            Employee[] defaults = {
                    new Employee("EMP001", "Alice Santos",   "Engineering", "08:00", "17:00"),
                    new Employee("EMP002", "Bob Reyes",      "HR",          "08:00", "17:00"),
                    new Employee("EMP003", "Cara Dela Cruz", "Finance",     "08:00", "17:00"),
                    new Employee("EMP004", "Dan Villanueva", "Engineering", "08:00", "17:00"),
                    new Employee("EMP005", "Eva Fernandez",  "Marketing",   "08:00", "17:00")
            };
            for (Employee e : defaults) {
                employeeRegistry.put(e.getId(), e);
            }
            CSVHandler.saveEmployees(getAllEmployeesList());
            System.out.println("Seeded default employees to employees.csv");
        } else {
            for (Employee e : loaded) {
                employeeRegistry.put(e.getId(), e);
            }
            System.out.println("Loaded " + loaded.size() + " employee(s) from file.");
        }
    }

    // Returns true if the admin username/password combination is valid.
    public boolean validateAdmin(String username, String password) {
        String stored = adminCredentials.get(username);
        return stored != null && stored.equals(password);
    }

    // Returns the Employee for the given ID, or null if not found.
    public Employee findEmployee(String employeeId) {
        return employeeRegistry.get(employeeId.toUpperCase());
    }

    // Returns true if the employee ID exists in the system.
    public boolean isValidEmployeeId(String employeeId) {
        return employeeRegistry.containsKey(employeeId.toUpperCase());
    }

    public Map<String, Employee> getAllEmployees() {
        return new HashMap<>(employeeRegistry);
    }

    private java.util.List<Employee> getAllEmployeesList() {
        return new java.util.ArrayList<>(employeeRegistry.values());
    }
}