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
 * A model representing a Salesman entity.
 * It extends RepresentationModel to provide HATEOAS links.
 */
@Getter
@Setter
@Schema(description = "Salesman entity representing a salesman")
public class SalesmanResponseModel extends RepresentationModel<SalesmanResponseModel> {

    @Schema(description = "Unique identifier of the salesman")
    private Integer id;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the salesman")
    private String lastName;

    @NotNull(message = "Firstname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the salesman")
    private String firstName;

    @NotNull(message = "Email must not be null or empty")
    @Email(message = "Email must be valid: example@example.fr")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Email address of the salesman")
    private String emailAddress;

    @NotNull(message = "Latitude must not be null or empty")
    @Schema(description = "Latitude of the salesman's home address")
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Schema(description = "Longitude of the salesman's home address")
    private double longHomeAddress;
}