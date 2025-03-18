/*
 * EmailAlreadyTakenException.java                                 06 déc. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.exceptions;

/**
 * Exception thrown when the email is already taken.
 */
public class EmailAlreadyTakenException extends IllegalArgumentException {

    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
