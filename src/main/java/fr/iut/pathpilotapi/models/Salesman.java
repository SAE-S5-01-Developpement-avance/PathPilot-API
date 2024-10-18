package fr.iut.pathpilotapi.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

/**
 * A salesman is defined by the following fields:
 *<p>
 * <h3>Mandatory fields</h3>
 * <ul>
 *  <li>First Name</li>
 *  <li>Last Name</li>
 *  <li>Password</li>
 *  <li>Email Address</li>
 *  <li>Home Address</li>
 *  </ul>
 * <h3>Optional fields</h3>
 * <ul>
 *  <li>Clients</li>
 *  <li>Prospects</li>
 *  <li>Routes</li>
 *  <li>Journeys</li>
 * </ul>
 */
@Entity
public class Salesman {

    private final int MAX_LENGTH = 100;
    private final int MAX_LENGTH_PWD = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String lastName;

    public String firstName;

    public String password;

    public String emailAddress;

    public String latHomeAddress;

    public String longHomeAddress;

    public Salesman(String lastName, String firstname, String plainTextPassword, String emailAddress, String latHomeAddress, String longHomeAddress) {
        if (lastName == null || firstname == null || plainTextPassword == null || emailAddress == null || latHomeAddress == null || longHomeAddress == null
                || lastName.isEmpty() || firstname.isEmpty() || plainTextPassword.isEmpty() || emailAddress.isEmpty()
                || latHomeAddress.isEmpty() || longHomeAddress.isEmpty()) {
            throw new IllegalArgumentException("All fields are mandatory");
        }

        if (lastName.length() >= MAX_LENGTH || firstname.length() >= MAX_LENGTH || plainTextPassword.length() >= MAX_LENGTH_PWD
                || emailAddress.length() >= MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("All fields must be less than %d characters", MAX_LENGTH + 1));
        }

        if (!emailAddress.matches("^[a-zA-Z0-9.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address, must be of the form: \"example@example.com\"");
        }

        if (!plainTextPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")) {
            throw new IllegalArgumentException(String.format("Password must be at least 8 characters long and at most %d characters", MAX_LENGTH_PWD));
        }

        if (!latHomeAddress.matches("^-?\\d{1,2}\\.\\d{1,10}$") || !longHomeAddress.matches("^-?\\d{1,3}\\.\\d{1,10}$")) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        this.lastName = lastName;
        this.firstName = firstname;
        this.password = plainTextPassword;
        this.emailAddress = emailAddress;
        this.latHomeAddress = latHomeAddress;
        this.longHomeAddress = longHomeAddress;
    }

    public Salesman() {
    }

    public static String hashPassword(String plainTextPassword) {
        // gensalt's log_rounds parameter determines the complexity
        // the work factor is 2**log_rounds, and the default is 10
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }
}
