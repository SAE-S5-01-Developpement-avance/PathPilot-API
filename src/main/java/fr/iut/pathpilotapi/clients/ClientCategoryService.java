/*
 * ClientCategoryService.java                                 28 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientCategoryService {

    public final ClientCategoryRepository clientCategoryRepository;

    public ClientCategory findByName(String name) {
        return clientCategoryRepository.findByName(name).orElseThrow(
                () -> new ObjectNotFoundException("Client category not found with name: " + name));
    }
}
