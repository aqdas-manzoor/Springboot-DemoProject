package net.demo.project.springbootemployeerepo.controller;

import net.demo.project.springbootemployeerepo.model.Employee;
import net.demo.project.springbootemployeerepo.repositories.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")  // Base URL for product-related endpoints
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    // HTTP GET request to get all employees
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllEmployees() {
        return new ResponseEntity<>(employeeDao.getAllEmployees(), HttpStatus.OK);
    }

    // HTTP GET request to get an employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable int id) {
        Map<String, Object> employee = employeeDao.getEmployeeById(id);  // Call DAO to fetch employee by ID

        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }
        /**
         *   ResponseEntity is a generic class in Spring that represents the entire HTTP response,
         *   including the body, headers, and status code.
         */
        return new ResponseEntity<>(employee, HttpStatus.OK);  // Return 200 OK with employee data
    }

    // HTTP POST request to create a new employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        // Insert the new employee into the database
        boolean isInserted = employeeDao.insertEmployee(employee);

        if (isInserted) {
            return new ResponseEntity<>(employee, HttpStatus.CREATED);  // Return 201 Created with employee data
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if insertion fails
        }
    }

    // HTTP PUT request to update an existing employee by ID
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        // Check if the employee exists first
        Map<String, Object> existingEmployee = employeeDao.getEmployeeById(id);
        if (existingEmployee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }

        // If employee exists, update the details
        boolean isUpdated = employeeDao.updateEmployee(id, employee);
        if (isUpdated) {
            return new ResponseEntity<>(employee, HttpStatus.OK);  // Return 200 OK if updated
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if update fails
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        // Check if the employee exists first
        Map<String, Object> existingEmployee = employeeDao.getEmployeeById(id);
        if (existingEmployee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }

        // Attempt to delete the employee
        boolean isDeleted = employeeDao.deleteEmployee(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Return 204 No Content on successful deletion
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if deletion fails
        }
    }
}