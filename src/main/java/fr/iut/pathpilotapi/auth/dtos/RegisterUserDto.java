/*
 * RegisterUserDto.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.dtos;

import lombok.Getter;
import lombok.Setter;

/**
 * @author François de Saint Palais
 */
@Getter
@Setter
public class RegisterUserDto {
    private String firstName;
    private String lastName;
    private double latitude;
    private double longitude;
    private String email;
    private String password;
}
