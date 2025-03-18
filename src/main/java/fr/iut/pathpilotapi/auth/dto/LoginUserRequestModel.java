/*
 * LoginUserRequestModel.java                             28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequestModel {
    @Email
    @Schema(description = "The email of the user", example = "example@example.fr")
    private String email;

    @Schema(description = "The password of the user", example = "password")
    @Size(min = 8)
    private String password;
}