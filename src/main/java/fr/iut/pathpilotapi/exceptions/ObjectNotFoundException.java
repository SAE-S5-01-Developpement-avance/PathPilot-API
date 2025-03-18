/*
 * ObjectNotFoundException.java                                 29 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.exceptions;

/**
 * Exception thrown when an object is not found.
 */
public class ObjectNotFoundException extends IllegalArgumentException {

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
