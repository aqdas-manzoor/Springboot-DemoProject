package net.demo.project.springbootemployeerepo.repositories;

import net.demo.project.springbootemployeerepo.model.Address;
import net.demo.project.springbootemployeerepo.model.Department;
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
                "a.id AS address_id, a.street, a.city, a.state, a.zip_code, a.phone_number, a.address_type, " +
                "d.id AS department_id, d.department_name, d.department_email, d.department_description " +
                "FROM employees e " +
                "LEFT JOIN address a ON e.id = a.employee_id " +
                "LEFT JOIN employee_departments ed ON e.id = ed.employee_id " +
                "LEFT JOIN departments d ON ed.department_id = d.id";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        return groupEmployeesWithAddressesAndDepartments(rows);
    }

    private List<Employee> groupEmployeesWithAddressesAndDepartments(List<Map<String, Object>> rows) {
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
                employee.setDepartments(new ArrayList<>());
                employeeMap.put(employeeId, employee);
            }

            // Map Address if it exists
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

            // Map Department if it exists
            if (row.get("department_id") != null) {
                Department department = new Department();
                department.setId((Integer) row.get("department_id"));
                department.setDepartmentName((String) row.get("department_name"));
                department.setDepartmentEmail((String) row.get("department_email"));
                department.setDepartmentDescription((String) row.get("department_description"));
                employee.getDepartments().add(department);
            }
        }

        return new ArrayList<>(employeeMap.values());
    }



    public Employee getEmployeeById(int id) {
        // SQL query to fetch the employee, their associated addresses, and departments
        String sql = "SELECT e.id AS employee_id, e.name, e.age, e.salary, e.email, " +
                "a.id AS address_id, a.street, a.city, a.state, a.zip_code, a.phone_number, a.address_type, " +
                "d.id AS department_id, d.department_name, d.department_email, d.department_description " +
                "FROM employees e " +
                "LEFT JOIN address a ON e.id = a.employee_id " +
                "LEFT JOIN employee_departments ed ON e.id = ed.employee_id " +
                "LEFT JOIN departments d ON ed.department_id = d.id " +
                "WHERE e.id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        // If no rows are returned, return null (no employee found)
        if (rows.isEmpty()) {
            return null;
        }

        // Now, process the results and map them into an Employee object with its associated addresses and departments
        return mapEmployeeWithAddressesAndDepartments(rows);
    }

    private Employee mapEmployeeWithAddressesAndDepartments(List<Map<String, Object>> rows) {
        Employee employee = new Employee();

        List<Department> departments = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        // Iterate through each row to set employee, address, and department information
        for (Map<String, Object> row : rows) {
            // Set employee basic details
            employee.setName((String) row.get("name"));
            employee.setAge((Integer) row.get("age"));
            employee.setSalary((Integer) row.get("salary"));
            employee.setEmail((String) row.get("email"));

            // Initialize the lists for addresses and departments if not already initialized
            if (employee.getAddresses() == null) {
                employee.setAddresses(new ArrayList<>());
            }
            if (employee.getDepartments() == null) {
                employee.setDepartments(new ArrayList<>());
            }

            // Map address details if present
            if (row.get("address_id") != null) {
                Address address = new Address();
                address.setStreet((String) row.get("street"));
                address.setCity((String) row.get("city"));
                address.setState((String) row.get("state"));
                address.setZipCode((Integer) row.get("zip_code"));
                address.setNumber((String) row.get("phone_number"));
                address.setAddressType((String) row.get("address_type"));
                addresses.add(address);
            }

            // Map department details if present
            if (row.get("department_id") != null) {
                Department department = new Department();
                department.setId((Integer) row.get("department_id"));
                department.setDepartmentName((String) row.get("department_name"));
                department.setDepartmentEmail((String) row.get("department_email"));
                department.setDepartmentDescription((String) row.get("department_description"));
                departments.add(department);
            }
        }

        // Set the final lists to the employee
        employee.setAddresses(addresses);
        employee.setDepartments(departments);

        return employee;
    }


    public boolean insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, age, salary, email) VALUES (?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, employee.getName(), employee.getAge(),
                    employee.getSalary(), employee.getEmail());

            if (rowsAffected == 1) {
                int employeeId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

                // Insert addresses
                for (Address address : employee.getAddresses()) {
                    String addressSql = "INSERT INTO address (street, city, state, zip_code, phone_number, address_type, employee_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(addressSql, address.getStreet(), address.getCity(), address.getState(),
                            address.getZipCode(), address.getNumber(), address.getAddressType(), employeeId);
                }

                // Insert into employee_department (many-to-many association)
                for (Department department : employee.getDepartments()) {
                    String departmentSql = "INSERT INTO employee_departments (employee_id, department_id) VALUES (?, ?)";
                    jdbcTemplate.update(departmentSql, employeeId, department.getId());
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
    public boolean updateEmployeeDepartments(int id, List<Department> departments) {
        // First, delete existing department associations for the employee
        String deleteSql = "DELETE FROM employee_departments WHERE employee_id = ?";
        jdbcTemplate.update(deleteSql, id);  // Remove existing department associations

        // Now, insert the new department associations
        String insertSql = "INSERT INTO employee_departments (employee_id, department_id) VALUES (?, ?)";

        for (Department department : departments) {
            jdbcTemplate.update(insertSql, id, department.getId());
        }

        return true;  // Return true if department associations are successfully updated
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
    public boolean deleteEmployeeDepartments(int id) {
        String sql = "DELETE FROM employee_departments WHERE employee_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected >= 0;  // Return true if rows were deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if there was an error
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