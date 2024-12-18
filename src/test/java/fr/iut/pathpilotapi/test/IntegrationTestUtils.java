/*
 * IntegrationTestUtils.java                                  08 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.routes.Route;
import fr.iut.pathpilotapi.routes.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.PositionDTO;
import fr.iut.pathpilotapi.salesman.Salesman;

import java.util.List;

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
     *     <li>latHomeAddress: 0.0</li>
     *     <li>longHomeAddress: 0.0</li>
     * </ul>
     * </p>
     *
     * @return a client with default values
     */
    public static Client createClient() {
        Client client = new Client();
        client.setCompanyName("Test Company" + System.currentTimeMillis());
        client.setLatHomeAddress(0.0);
        client.setLongHomeAddress(0.0);
        return client;
    }

    /**
     * Create a client with required fields.
     * <p>
     * The client is created with the following values:
     * <ul>
     *     <li>companyName: "Test Company" + current time in milliseconds</li>
     *     <li>latHomeAddress: 0.0</li>
     *     <li>longHomeAddress: 0.0</li>
     *     <li>salesman : get from param</li>
     * </ul>
     * </p>
     *
     * @param salesman Tha salesman the client belong to
     * @return a client with default values
     */
    public static Client createClient(Salesman salesman) {
        Client client = createClient();
        client.setSalesman(salesman);

        return client;
    }

    /**
     * Create a salesman with required fields.
     * <p>
     * The salesman is created with the following values:
     * <ul>
     *     <li>firstName: "John"</li>
     *     <li>lastName: "Doe"</li>
     *     <li>password: "password"</li>
     *     <li>emailAddress: "john.doe@test.com"</li>
     *     <li>latHomeAddress: "0.0"</li>
     *     <li>longHomeAddress: "0.0"</li>
     * </ul>
     * </p>
     *
     * @return a salesman with default values
     */
    public static Salesman createSalesman() {
        Salesman salesman = new Salesman();
        salesman.setFirstName("John");
        salesman.setLastName("Doe");
        salesman.setPassword("password");
        salesman.setEmailAddress(System.currentTimeMillis() + "john.doe@test.com");
        salesman.setLatHomeAddress(0.0);
        salesman.setLongHomeAddress(0.0);
        return salesman;
    }

    public static Salesman createSalesman(String email, String password) {
        Salesman salesman = createSalesman();
        salesman.setEmailAddress(email);
        salesman.setPassword(password);
        return salesman;
    }

    /**
     * Convert an Object to a JSON string.
     *
     * @param object the object to convert
     * @return the JSON string
     */
    public static String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Route createRoute(Salesman salesman, List<Client> clients) {
        Route route = new Route();
        PositionDTO position = new PositionDTO();

        position.setLatitude(salesman.getLatHomeAddress());
        position.setLongitude(salesman.getLongHomeAddress());

        route.set_id((int) System.nanoTime());
        route.setSalesman(salesman.getId());
        route.setSalesmanHome(position);
        route.setClients_schedule(clients.stream()
                .map(ClientDTO::createFromClient)
                .toList()
        );

        return route;
    }
}
