/*
 * RouteClient.java                                 07 Feb 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;

/**
 * Class representing a client in a route
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Client</li>
 *     <li>State</li>
 * </ul>
 * @see ClientDTO
 * @see ClientState
 */
public record RouteClient(ClientDTO client , ClientState state) {
}
