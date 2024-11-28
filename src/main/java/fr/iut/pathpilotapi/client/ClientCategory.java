/*
 * ClientType.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

/**
 * Enum representing different category of clients.
 *
 * <p>Possible values are:
 * <ul>
 *     <li>{@link #CLIENT}: Represents an active client.</li>
 *     <li>{@link #PROSPECT}: Represents a prospective client.</li>
 * </ul>
 */
public enum ClientCategory {
    CLIENT,
    PROSPECT
}
