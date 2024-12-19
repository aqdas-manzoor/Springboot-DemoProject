package net.demo.project.springbootemployeerepo.model;

/**
 * The {@code Address} class represents an address for an employee.
 */
public class Address {


    private String street;

    /**
     * Type of the address (e.g., home, office)
     */
    private String addressType;

    /**
     * City of the address
     */
    private String city;

    /**
     * State of the address
     */
    private String state;

    /**
     * Zip code of the address
     */
    private int zipCode;

    /**
     * Contact number for the address
     */
    private String number;


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
