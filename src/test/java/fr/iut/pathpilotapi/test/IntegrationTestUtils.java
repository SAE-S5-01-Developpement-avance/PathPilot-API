/*
 * IntegrationTestUtils.java                                  08 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.test;

import fr.iut.pathpilotapi.client.Client;

/**
 * Utility class for integration tests.
 */
public class IntegrationTestUtils {

    /**
     * Create a client with required fields.
     * <p>
     * The client is created with the following values:
     * <ul>
     *     <li>companyName: "Test Company" + current time in milliseconds</li>
     *     <li>latHomeAddress: "0.0"</li>
     *     <li>longHomeAddress: "0.0"</li>
     * </ul>
     * </p>
     *
     * @return a client with default values
     */
    public static Client createClient() {
        Client client = new Client();
        client.setCompanyName("Test Company" + System.currentTimeMillis());
        client.setLatHomeAddress("0.0");
        client.setLongHomeAddress("0.0");
        return client;
    }

}
