/*
 * IntegrationTestUtils.java                                  08 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientCategory;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.itineraries.routes.Route;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.salesman.Salesman;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.UUID;

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
        salesman.setEmailAddress("john.doe@test.com");
        salesman.setLatHomeAddress(0.0);
        salesman.setLongHomeAddress(0.0);
        return salesman;
    }

    /**
     * Create a client category with required fields.
     * <p>
     * The client category is created with the following values:
     * <ul>
     *     <li>name: "Prospect"</li>
     * </ul>
     * </p>
     *
     * @return a client category with default values
     */
    public static ClientCategory createClientCategory() {
        ClientCategory clientCategory = new ClientCategory();
        clientCategory.setName("PROSPECT");
        return clientCategory;
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

    public static Route createRoute(Salesman salesman, List<ClientDTO> clients) {
        Route route = new Route();
        GeoJsonPoint position = new GeoJsonPoint(salesman.getLongHomeAddress(), salesman.getLatHomeAddress());

        route.setId(UUID.randomUUID().toString());
        route.setSalesman_id(salesman.getId());
        route.setSalesman_home(position);
        route.setExpected_clients(clients);

        return route;
    }

    /**
     * <p>
     * Create an itinerary with required fields.
     * </ul>
     * The itinerary is created with the following values:
     * <ul>
     *     <li>salesman_id: 1</li>
     *    <li>salesman_home: (0.0, 0.0)</li>
     *    <li>clients_schedule: empty list</li>
     *   </p>
     * @param salesman the itinerary belongs to
     * @return an itinerary with default values
     */
    public static Itinerary createItinerary(Salesman salesman, List<ClientDTO> clients) {
        Itinerary itinerary = new Itinerary();
        GeoJsonPoint position = new GeoJsonPoint(salesman.getLatHomeAddress(), salesman.getLongHomeAddress());

        itinerary.setId(UUID.randomUUID().toString());
        itinerary.setSalesman_id(salesman.getId());
        itinerary.setSalesman_home(position);
        itinerary.setClients_schedule(clients);

        return itinerary;
    }

    /**
     * <p>
     * Create an itinerary request model with required fields.
     * The itinerary request model is created with the following values:
     * <ul>
     *     <li>salesman_id: 1</li>
     *    <li>salesman_home: (0.0, 0.0)</li>
     *   <li>clients_schedule: empty list</li>
     * </p>
     * @return an itinerary request model with default values
     */
    public static ItineraryRequestModel createItineraryRequestModel(List<ClientDTO> clientsSchedule) {
        ItineraryRequestModel itineraryRequestModel = new ItineraryRequestModel();

        itineraryRequestModel.setClientsSchedule(clientsSchedule);

        return itineraryRequestModel;
    }

    /**
     * <p>
     * Create a list of clients with required fields.
     * @return a list of clients with default values
     */
    public static Iterable<Client> createClients(Salesman salesman) {
        return List.of(createClient(salesman), createClient(salesman));
    }
}
