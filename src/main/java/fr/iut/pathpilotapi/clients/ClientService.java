/*
 * ClientService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.clients.repository.MongoClientRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.exceptions.SalesmanBelongingException;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    private final MongoClientRepository mongoClientRepository;

    private final ClientCategoryService clientCategoryService;

    /**
     * Get all clients that belong to the connected salesman.
     *
     * @param salesman    the connected salesman
     * @param pageRequest the pageable object that specifies the page to retrieve with size and sorting
     * @return a page of all clients that belongs to the connected salesman
     */
    public Page<Client> getAllClientsBySalesmanPageable(Salesman salesman, Pageable pageRequest) {
        return clientRepository.findAllBySalesman(salesman, pageRequest);
    }

    /**
     * Get all clients that belong to the connected salesman.
     *
     * @param salesman the connected salesman
     * @return a list of all clients that belongs to the connected salesman
     */
    public List<Client> getAllClientsBySalesman(Salesman salesman) {
        return clientRepository.findAllBySalesman(salesman);
    }

    /**
     * Create a new client in the database.
     *
     * @param clientRM the salesman to create
     * @param salesman the connected salesman
     * @return the newly created salesman
     */
    public Client addClient(ClientRequestModel clientRM, Salesman salesman) {
        //Map the request model to the entity
        Client client = new Client();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(clientRM, client);
        client.setClientCategory(clientCategoryService.findByName(clientRM.getClientCategory()));

        //Set remaining client fields
        client.setSalesman(salesman);
        Client savedClient = clientRepository.save(client);

        // Save the lite version of the client in MongoDB
        MongoClient liteClient = new MongoClient(savedClient.getId(), clientRM.getLatHomeAddress(), clientRM.getLongHomeAddress());
        mongoClientRepository.save(liteClient);

        return clientRepository.save(client);
    }

    /**
     * Delete a client, if the connected salesman is the one related to the client.
     *
     * @param id       the client id
     * @param salesman the connected salesman
     * @throws IllegalArgumentException if the client is not found or does not belong to the salesman
     */
    public void deleteByIdAndConnectedSalesman(Integer id, Salesman salesman) {
        Client client = findByIdAndConnectedSalesman(id, salesman);

        // Perform the delete operation
        clientRepository.delete(client);
    }

    /**
     * Get a client by its id and the connected salesman
     *
     * @param id       the id of the client
     * @param salesman the connected salesman
     * @return the client
     * @throws ObjectNotFoundException  if the client is not found
     * @throws IllegalArgumentException if the client does not belong to the salesman
     */
    public Client findByIdAndConnectedSalesman(Integer id, Salesman salesman) {
        Client client = clientRepository.findById(id).orElseThrow(() -> {
            logger.error("Client not found with ID: {}", id);
            return new ObjectNotFoundException("Client not found with ID: " + id);
        });

        // Check if the client belongs to the connected salesman
        if (!clientBelongToSalesman(client, salesman)) {
            throw new SalesmanBelongingException("Client with ID: " + id + " does not belong to the connected salesman.");
        }
        return client;
    }

    /**
     * Check if the client belongs to the salesman.
     *
     * @param client   the client to check
     * @param salesman the salesman to check
     * @return true if the client belongs to the salesman, false otherwise
     */
    public boolean clientBelongToSalesman(Client client, Salesman salesman) {
        if (client == null) {
            throw new ObjectNotFoundException("Client does not exist");
        }
        return salesman.equals(client.getSalesman());
    }

    /**
     * Get all clients from a list of clients id.
     * <p>
     * Check if all clients exist.
     * If not, throw an exception.
     * Check if all clients belong to the salesman.
     * If not, throw an exception.
     *
     * @param clientsSchedule the list of clients id
     * @param salesman        the salesman
     * @return the list of clients
     * @throws IllegalArgumentException if a client does not exist or does not belong to the salesman
     */
    public List<Client> getAllClients(List<Integer> clientsSchedule, Salesman salesman) {
        List<Client> clients = clientRepository.findAllById(clientsSchedule);
        // Sorting the clients list after findAll
        List<Client> orderedClients = clientsSchedule.stream()
                .map(id -> clients.stream()
                        .filter(client -> client.getId() == id)
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull).toList();

        if (orderedClients.size() != clientsSchedule.size()) {
            throw new IllegalArgumentException("Client not found");
        }
        if (!orderedClients.stream().allMatch(client -> clientBelongToSalesman(client, salesman))) {
            throw new IllegalArgumentException("Client does not belong to the salesman");
        }
        return orderedClients;
    }

    /**
     * Return the locations for the clients specified.
     * @param clientsSchedule   list of ID of the clients.
     * @return the list of locations
     */
    public List<List<Double>> getClientsLocations(List<Client> clientsSchedule) {
        return clientsSchedule.stream()
                .map(client -> Arrays.asList(client.getLatHomeAddress(),client.getLongHomeAddress()))
                .toList();
    }
}
