/*
 * PersonalInfoRequestModel.java                                 06 mars 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.salesman.dto.PasswordChangeRequestModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

@Getter
public class PersonalInfoRequestModel {

    @Schema(description = "Last name of the salesman", example = "Doe")
    @Size(max = MAX_LENGTH)
    private String lastName;

    @Schema(description = "First name of the salesman", example = "John")
    @Size(max = MAX_LENGTH)
    private String firstName;

    @Email(message = "Email must be valid: example@example.fr")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Email address of the salesman", example = "john.doe@example.com")
    private String emailAddress;

    @Schema(description = "Former and new salesman's passwords")
    private PasswordChangeRequestModel passwordChangeRequestModel;
}
