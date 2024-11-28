/*
 * ClientService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Get all clients from the database
     *
     * @return a page of all clients
     */
    public Page<Client> getAllClients(PageRequest pageRequest) {
        return clientRepository.findAll(pageRequest);
    }

    /**
     * Create a new client in the database.
     *
     * @param client the salesman to create
     * @return the newly created salesman
     */
    public Client addClient(Client client) {
        return clientRepository.save(client);
    }

    /**
     * Delete a client by its id.
     *
     * @param id the id of the client
     * @throws IllegalArgumentException if the client is not found
     */
    public boolean deleteById(Integer id) {
        try {
            clientRepository.deleteById(id);
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
}
