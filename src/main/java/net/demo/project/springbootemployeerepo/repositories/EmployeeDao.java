package net.demo.project.springbootemployeerepo.repositories;

import net.demo.project.springbootemployeerepo.model.Address;
import net.demo.project.springbootemployeerepo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Employee> getAllEmployees() {
        String sql = "SELECT e.id AS employee_id, e.name, e.age, e.salary, e.email, " +
                "a.id AS address_id, a.street, a.city, a.state, a.zip_code, a.phone_number, a.address_type " +
                "FROM employees e " +
                "LEFT JOIN address a ON e.id = a.employee_id";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        return groupEmployeesWithAddresses(rows);
    }

    private List<Employee> groupEmployeesWithAddresses(List<Map<String, Object>> rows) {
        Map<Integer, Employee> employeeMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            int employeeId = (Integer) row.get("employee_id");
            Employee employee = employeeMap.get(employeeId);

            if (employee == null) {
                // Create a new Employee object if it's not already in the map
                employee = new Employee();
                employee.setName((String) row.get("name"));
                employee.setAge((Integer) row.get("age"));
                employee.setSalary((Integer) row.get("salary"));
                employee.setEmail((String) row.get("email"));
                employee.setAddresses(new ArrayList<>());
                employeeMap.put(employeeId, employee);
            }

            // Check if address is not null before creating an Address object
            if (row.get("address_id") != null) {
                Address address = new Address();
                address.setStreet((String) row.get("street"));
                address.setCity((String) row.get("city"));
                address.setState((String) row.get("state"));
                address.setZipCode((Integer) row.get("zip_code"));
                address.setNumber((String) row.get("phone_number"));
                address.setAddressType((String) row.get("address_type"));
                employee.getAddresses().add(address);
            }
        }

        return new ArrayList<>(employeeMap.values());
    }


    public Employee getEmployeeById(int id) {
        // SQL query to fetch the employee and their associated addresses
        String sql = "SELECT e.id AS employee_id, e.name, e.age, e.salary, e.email, " +
                "a.id AS address_id, a.street, a.city, a.state, a.zip_code, a.phone_number, a.address_type " +
                "FROM employees e " +
                "LEFT JOIN address a ON e.id = a.employee_id " +
                "WHERE e.id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        // If no rows are returned, return null (no employee found)
        if (rows.isEmpty()) {
            return null;
        }

        // Now, process the results and map them into an Employee object with its associated addresses
        return mapEmployeeWithAddresses(rows);
    }

    private Employee mapEmployeeWithAddresses(List<Map<String, Object>> rows) {
        // The employee object to return
        Employee employee = new Employee();

        // Since we are joining employees with addresses, each row could represent one employee with one address
        // We need to handle this by populating the Employee object and its associated Addresses
        for (Map<String, Object> row : rows) {
            // First, set employee details
            employee.setName((String) row.get("name"));
            employee.setAge((Integer) row.get("age"));
            employee.setSalary((Integer) row.get("salary"));
            employee.setEmail((String) row.get("email"));

            // Initialize the addresses list if not already done
            if (employee.getAddresses() == null) {
                employee.setAddresses(new ArrayList<>());
            }

            // Add the address to the employee
            Address address = new Address();
            address.setStreet((String) row.get("street"));
            address.setCity((String) row.get("city"));
            address.setState((String) row.get("state"));
            address.setZipCode((Integer) row.get("zip_code"));
            address.setNumber((String) row.get("phone_number"));
            address.setAddressType((String) row.get("address_type"));

            employee.getAddresses().add(address);
        }

        return employee;
    }

    public boolean insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, age, salary, email) VALUES (?, ?, ?, ?)";
        try {
            // Insert the employee into the database
            int rowsAffected = jdbcTemplate.update(sql, employee.getName(), employee.getAge(),
                    employee.getSalary(), employee.getEmail());

            // Assuming that the insert is successful, now get the generated employee ID
            if (rowsAffected == 1) {
                int employeeId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

                // Insert the addresses for this employee
                for (Address address : employee.getAddresses()) {
                    String addressSql = "INSERT INTO address (street, city, state, zip_code, phone_number, address_type, employee_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(addressSql, address.getStreet(), address.getCity(), address.getState(),
                            address.getZipCode(), address.getNumber(), address.getAddressType(), employeeId);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployeeDetails(int id, Employee employee) {
        String sql = "UPDATE employees SET name = ?, age = ?, salary = ?, email = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, employee.getName(), employee.getAge(),
                    employee.getSalary(), employee.getEmail(), id);
            return rowsAffected == 1;  // Return true if one row is affected
        } catch (Exception e) {
            e.printStackTrace();  // Print error for debugging
            return false;
        }
    }
    public boolean updateEmployeeAddresses(int id, List<Address> addresses) {
        // First, delete existing addresses for the employee
        String deleteSql = "DELETE FROM address WHERE employee_id = ?";
        jdbcTemplate.update(deleteSql, id);  // Remove existing addresses

        // Now, insert the new addresses provided in the request
        String insertSql = "INSERT INTO address (street, city, state, zip_code, phone_number, address_type, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        for (Address address : addresses) {
            jdbcTemplate.update(insertSql, address.getStreet(), address.getCity(), address.getState(),
                    address.getZipCode(), address.getNumber(), address.getAddressType(), id);
        }

        return true;  // Return true if addresses are successfully updated
    }
    public boolean deleteEmployeeAddresses(int id) {
        String sql = "DELETE FROM address WHERE employee_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected >= 1;  // Return true if one or more rows were deleted
        } catch (Exception e) {
            e.printStackTrace();  // Print error for debugging
            return false;
        }
    }
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected == 1;  // Return true if exactly one row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if there was an error
        }
    }


}