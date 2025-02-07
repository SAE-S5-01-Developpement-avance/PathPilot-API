/*
 * ClientRequestModel.java                                 28 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

@Getter
@Setter
@Schema(description = "Client entity representing a client or prospect")
public class ClientRequestModel {

    @NotBlank(message = "Company name must not be null or empty")
    @Schema(description = "Name of the company", example = "IKEA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @NotNull(message = "Latitude must not be null or empty")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    @Schema(description = "Latitude of the company's location", example = "48.8566", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    @Schema(description = "Longitude of the company's location", example = "2.3522", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longHomeAddress;

    @Schema(description = "Type of the client", example = "CLIENT")
    @Pattern(regexp = "CLIENT|PROSPECT", message = "Client category must be either 'CLIENT' or 'PROSPECT'")
    private String clientCategory;

    @Size(max = 255)
    @Schema(description = "Description of the client", example = "Description A")
    private String description;

    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the contact person", example = "Doe")
    private String contactLastName;

    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the contact person", example = "John")
    private String contactFirstName;

    @Pattern(regexp = "(\\d{10})|()")
    @Schema(description = "Phone number of the contact person", example = "0123456789")
    private String phoneNumber;
}
