package util;

/**
 * Utility class for validating user information.
 * It provides methods to validate user names, email addresses, addresses, and phone numbers.
 */
public class UserValidator {
	
	 /**
     * Constructeur par défaut.
     */
    public UserValidator() {
        // Default constructor
    }

    /**
     * Validates a user's name (either first name or last name).
     * 
     * @param name The name to validate.
     * @return True if the name is valid, false otherwise.
     */
    public static boolean isValidName(String name) {
        // Regex to accept letters (including accented letters), hyphens, apostrophes, and spaces
        String regex = "^[\\p{L} '-]+$";
        return name.matches(regex);
    }

    /**
     * Validates a user's email address.
     * 
     * @param email The email to validate.
     * @return True if the email is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    /**
     * Validates a user's address.
     * 
     * @param address The address to validate.
     * @return True if the address is valid, false otherwise.
     */
    public static boolean isValidAddress(String address) {
        // Simplified example regex for address validation
        String regex = "^[\\p{L}0-9 .,'/-]+$";
        return address.matches(regex);
    }

    /**
     * Validates a user's phone number (French format).
     * 
     * @param phone The phone number to validate.
     * @return True if the phone number is valid, false otherwise.
     */
    public static boolean isValidPhone(String phone) {
        // Allows formats with or without spaces, with or without the international prefix
        String regex = "^(\\+33[1-9]|0[1-9])(\\d{2}){4}$";
        return phone.matches(regex);
    }
}
