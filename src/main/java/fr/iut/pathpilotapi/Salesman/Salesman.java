package fr.iut.pathpilotapi.Salesman;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A salesman is defined by the following fields:
 * <p>
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
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Salesman {

    private final int MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    private String lastName;

    @NotNull(message = "Firstname must not be null or empty")
    @Size(max = MAX_LENGTH)
    private String firstName;

    /**
     * Password haché, traité dans le service
     */
    @NotNull(message = "Password must not be null or empty")
    private String password;

    @NotNull(message = "Email must not be null or empty")
    @Email( message = "Email must be valid : example@example.fr")
    @Size(max = MAX_LENGTH)
    private String emailAddress;

    @NotNull(message = "Latitude must not be null or empty")
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    private double longHomeAddress;
}
