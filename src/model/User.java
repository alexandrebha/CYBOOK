package model;

/**
 * Represents a user in the library system, containing user information such as ID, 
 * name, email, address, and phone number.
 */
public class User {
    private int id; // Unique identifier for the user
    private String lastName; // Last name of the user
    private String firstName; // First name of the user
    private String email; // Email address of the user
    private String address; // Address of the user
    private String phone; // Phone number of the user

    /**
     * Constructs a User object with the specified details.
     *
     * @param id        Unique identifier for the user.
     * @param lastName  Last name of the user.
     * @param firstName First name of the user.
     * @param email     Email address of the user.
     * @param address   Address of the user.
     * @param phone     Phone number of the user.
     */
    public User(int id, String lastName, String firstName, String email, String address, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    /**
     * Gets the address of the user.
     *
     * @return The address of the user.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the user.
     *
     * @param address The address of the user.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return The phone number of the user.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phone The phone number of the user.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier for the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The unique identifier for the user.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return A string representation of the user, including first name, last name, and ID.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " [ID: " + id + "]";
    }
}
