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
@RequestMapping("/employees")  // Base URL for employee-related endpoints
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeDao.getAllEmployees(), HttpStatus.OK);
    }

    // HTTP GET request to get an employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        Employee employee = employeeDao.getEmployeeById(id);  // Call DAO to fetch employee by ID

        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }

        // Return 200 OK with the employee data
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    // HTTP POST request to create a new employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        boolean isInserted = employeeDao.insertEmployee(employee);

        if (isInserted) {
            return new ResponseEntity<>(employee, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        // First, check if the employee exists
        Employee existingEmployee = employeeDao.getEmployeeById(id);
        if (existingEmployee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }

        // Update the employee's core details (name, age, salary, email)
        boolean isUpdated = employeeDao.updateEmployeeDetails(id, employee);
        if (!isUpdated) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if update fails
        }

        // Update the employee's addresses
        boolean isAddressUpdated = employeeDao.updateEmployeeAddresses(id, employee.getAddresses());
        if (!isAddressUpdated) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if address update fails
        }

        // Update the employee's departments
        boolean isDepartmentUpdated = employeeDao.updateEmployeeDepartments(id, employee.getDepartments());
        if (!isDepartmentUpdated) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if department update fails
        }

        // Return 200 OK with the updated employee data
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        // Check if the employee exists
        Employee existingEmployee = employeeDao.getEmployeeById(id);
        if (existingEmployee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if employee not found
        }

        // Delete the employee's associated departments
        boolean isDepartmentsDeleted = employeeDao.deleteEmployeeDepartments(id);
        if (!isDepartmentsDeleted) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if deleting departments fails
        }

        // Delete the employee's associated addresses
        boolean isAddressesDeleted = employeeDao.deleteEmployeeAddresses(id);
        if (!isAddressesDeleted) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if deleting addresses fails
        }

        // Delete the employee's core information
        boolean isEmployeeDeleted = employeeDao.deleteEmployee(id);
        if (!isEmployeeDeleted) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 if deleting employee fails
        }

        // Return 204 No Content on successful deletion
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}