/*
 * NotBelongToSalesman.java                                 14 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.exceptions;

/**
 * Exception thrown when the itinerary does not belong to the salesman.
 */
public class SalesmanBelongingException extends IllegalArgumentException {
    public SalesmanBelongingException(String message) {
        super(message);
    }
}