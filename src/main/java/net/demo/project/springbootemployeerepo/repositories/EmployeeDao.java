package net.demo.project.springbootemployeerepo.repositories;

import net.demo.project.springbootemployeerepo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Method to get all products
    public List<Map<String, Object>> getAllEmployees() {
        String sql = "SELECT * FROM employees";  // SQL query to fetch all products
        return jdbcTemplate.queryForList(sql);  // Execute SQL and return results
    }

    // Method to get a product by its ID
    public Map<String, Object> getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);  // Return single employee as a map
        } catch (EmptyResultDataAccessException e) {
            return null;  // Return null if no result found
        }  // Return single product as a map
    }

    // Method to insert a new employee into the database
    public boolean insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, age, salary, email) VALUES (?, ?, ?, ?)";
        try {
            // Execute insert SQL query
            int rowsAffected = jdbcTemplate.update(sql, employee.getName(), employee.getAge(),
                    employee.getSalary(), employee.getEmail());

            // If one row is inserted, return true
            return rowsAffected == 1;
        } catch (Exception e) {
            return false;  // Return false in case of any error
        }
    }

    // Method to update an employee's details
    public boolean updateEmployee(int id, Employee employee) {
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

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected > 0;  // Return true if one row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if an error occurred
        }
    }
}