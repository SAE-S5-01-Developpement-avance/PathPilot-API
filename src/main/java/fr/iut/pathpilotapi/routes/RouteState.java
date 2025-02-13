/*
 * RouteState.java                                 13 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

/**
 * Enum representing the state of a route
 */
public enum RouteState {
    /**
     * The route is not started yet
     */
    NOT_STARTED,
    /**
     * The route is in progress
     */
    IN_PROGRESS,
    /**
     * The route is paused
     */
    PAUSED,
    /**
     * The route is finished
     */
    FINISHED
}
