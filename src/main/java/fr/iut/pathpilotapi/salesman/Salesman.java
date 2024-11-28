/*
 * Salesman.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

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
@Schema(description = "Salesman entity representing a salesman")
public class Salesman {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the salesman", example = "1")
    private Integer id;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the salesman", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotNull(message = "Firstname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the salesman", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /**
     * Hashed password, treated in service
     */
    @NotNull(message = "Password must not be null or empty")
    @Schema(description = "Hashed password of the salesman", example = "hashedpassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotNull(message = "Email must not be null or empty")
    @Email(message = "Email must be valid: example@example.fr")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Email address of the salesman", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailAddress;

    @NotNull(message = "Latitude must not be null or empty")
    @Schema(description = "Latitude of the salesman's home address", example = "48.8566", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Schema(description = "Longitude of the salesman's home address", example = "2.3522", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longHomeAddress;

    @OneToMany(mappedBy = "salesman")
    private Set<Client> clients = new HashSet<>();

}