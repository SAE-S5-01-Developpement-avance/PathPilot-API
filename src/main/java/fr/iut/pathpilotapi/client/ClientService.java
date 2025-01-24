/*
 * ClientService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.client.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private final ClientCategoryRepository clientCategoryRepository;

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
     * @param client   the salesman to create
     * @param salesman the connected salesman
     * @return the newly created salesman
     */
    public Client addClient(Client client, Salesman salesman) {
        ClientCategory clientCategory = clientCategoryRepository.findByName(client.getClientCategory().getName());
        client.setClientCategory(clientCategory);
        client.setSalesman(salesman);
        return clientRepository.save(client);
    }

    /**
     * Delete a client, if the connected salesman is the one related to the client.
     *
     * @param client the client to delete
     * @throws IllegalArgumentException if the client is not found
     */
    public boolean delete(Client client) {
        try {
            clientRepository.delete(client);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get a client by its id.
     *
     * @param id the id of the client
     * @return the client
     * @throws IllegalArgumentException if the client is not found
     */
    public Client getClientById(Integer id) {
        return clientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Client not found"));
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
            throw new IllegalArgumentException("Client not exist");
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
