/*
 * UserNotFoundException.java                                 06 déc. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.auth.exceptions;

/**
 * Exception thrown when the user is not found.
 */
public class UserNotFoundException extends IllegalArgumentException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
