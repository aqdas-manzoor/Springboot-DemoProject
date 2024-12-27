package net.demo.project.springbootemployeerepo.model;

/**
 * Represents a Department within an organization.
 * Contains details such as the department's name, email, description, and a unique ID.
 */
public class Department {
    private int id;
    private String departmentName;
    private String departmentEmail;
    private String departmentDescription;

    /**
     * Gets the description of the department.
     *
     * @return The description of the department.
     */
    public String getDepartmentDescription() {
        return departmentDescription;
    }

    /**
     * Gets the email address associated with the department.
     *
     * @return The email of the department.
     */
    public String getDepartmentEmail() {
        return departmentEmail;
    }

    /**
     * Sets the description of the department.
     *
     * @param departmentDescription The description to set.
     */
    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    /**
     * Sets the email address for the department.
     *
     * @param departmentEmail The email to set.
     */
    public void setDepartmentEmail(String departmentEmail) {
        this.departmentEmail = departmentEmail;
    }

    /**
     * Gets the name of the department.
     *
     * @return The name of the department.
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the name of the department.
     *
     * @param departmentName The name to set.
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Gets the unique ID of the department.
     *
     * @return The ID of the department.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the department.
     *
     * @param id The ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

}
