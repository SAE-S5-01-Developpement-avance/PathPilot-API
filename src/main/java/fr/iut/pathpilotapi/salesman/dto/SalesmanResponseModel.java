/*
 * SalesmanResponseModel.java                                 03 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

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
 */
@Getter
@Setter
@Schema(description = "Salesman entity representing a salesman")
public class SalesmanResponseModel extends RepresentationModel<SalesmanResponseModel> {

    @Schema(description = "Unique identifier of the salesman", example = "1")
    private Integer id;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the salesman", example = "Doe")
    private String lastName;

    @NotNull(message = "Firstname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the salesman", example = "John")
    private String firstName;

    @NotNull(message = "Email must not be null or empty")
    @Email(message = "Email must be valid: example@example.fr")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Email address of the salesman", example = "john.doe@example.com")
    private String emailAddress;

    @NotNull(message = "Latitude must not be null or empty")
    @Schema(description = "Latitude of the salesman's home address", example = "48.8566")
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Schema(description = "Longitude of the salesman's home address", example = "2.3522")
    private double longHomeAddress;
}