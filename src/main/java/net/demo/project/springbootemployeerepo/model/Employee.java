package net.demo.project.springbootemployeerepo.model;

/**
 * The {@code Employee} class represents an employee with attributes such as age, name, salary, email,
 *
 * <p>
 * This class provides methods to set and get the details of an employee.
 * </p>
 */
public class Employee {

    /**
     * Employee's age
     */
    private int age;

    /**
     * Employee's name
     */
    private String name;

    /**
     * Employee's salary
     */
    private int salary;

    /**
     * Employee's email
     */
    private String email;


    /**
     * Sets the salary of the employee.
     *
     * @param salary The salary to set.
     */
    public void setSalary(int salary) {
        this.salary = salary;
    }

    /**
     * Sets the name of the employee.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the age of the employee.
     *
     * @param age The age to set.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the email address of the employee.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the name of the employee.
     *
     * @return The name of the employee.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the email address of the employee.
     *
     * @return The email address of the employee.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the age of the employee.
     *
     * @return The age of the employee.
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the salary of the employee.
     *
     * @return The salary of the employee.
     */
    public int getSalary() {
        return salary;
    }


}
