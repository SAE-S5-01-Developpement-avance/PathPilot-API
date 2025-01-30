/*
 * ClientService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.auth.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

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
     * @param salesman    the connected salesman
     * @return a list of all clients that belongs to the connected salesman
     */
    public List<Client> getAllClientsBySalesman(Salesman salesman) {
        return clientRepository.findAllBySalesman(salesman);
    }

    /**
     * Create a new client in the database.
     *
     * @param clientRM   the salesman to create
     * @param salesman   the connected salesman
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
        return clientRepository.save(client);
    }

    /**
     * Delete a client, if the connected salesman is the one related to the client.
     *
     * @param id the client id
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
     * @param id the id of the client
     * @param salesman the connected salesman
     * @return the client
     * @throws ObjectNotFoundException if the client is not found
     * @throws IllegalArgumentException if the client does not belong to the salesman
     */
    public Client findByIdAndConnectedSalesman(Integer id, Salesman salesman) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Client not found with ID: " + id));

        // Check if the client belongs to the connected salesman
        if (!clientBelongToSalesman(client, salesman)) {
            throw new IllegalArgumentException("Client with ID: " + id + " does not belong to the connected salesman.");
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
    // TODO test when client is null
    public boolean clientBelongToSalesman(Client client, Salesman salesman) {
        if (client == null) {
            throw new IllegalArgumentException("Client does not exist");
        }
        return salesman.equals(client.getSalesman());
    }

    /**
     * Get all clients from a list of clients id.
     * <p>
     *     Check if all clients exist.
     *     If not, throw an exception.
     *     Check if all clients belong to the salesman.
     *     If not, throw an exception.
     *
     * @param clientsSchedule the list of clients id
     * @param salesman       the salesman
     * @return the list of clients
     * @throws IllegalArgumentException if a client does not exist or does not belong to the salesman
     */
    public List<Client> getAllClients(List<Integer> clientsSchedule, Salesman salesman) {
        List<Client> clients = clientRepository.findAllById(clientsSchedule);
        if (clients.size() != clientsSchedule.size()) {
            throw new IllegalArgumentException("Client not found");
        }
        if (!clients.stream().allMatch(client -> clientBelongToSalesman(client, salesman))) {
            throw new IllegalArgumentException("Client does not belong to the salesman");
        }
        return clients;
    }
}
