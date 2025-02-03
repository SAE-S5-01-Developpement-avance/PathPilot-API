/*
 * RegisterUserDto.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequestModel {

    @Schema(description = "The first name of the user", example = "John")
    @Size(min = 1, max = 100)
    private String firstName;

    @Schema(description = "The last name of the user", example = "Doe")
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull(message = "Latitude must not be null or empty")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    @Schema(description = "Latitude of the company's location", example = "48.8566", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latitude;

    @NotNull(message = "Longitude must not be null or empty")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    @Schema(description = "Longitude of the company's location", example = "2.3522", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longitude;

    @Email
    @Schema(description = "The email of the user", example = "example@example.fr")
    private String email;

    @Schema(description = "The password of the user", example = "password")
    @Size(min = 8)
    private String password;
}
