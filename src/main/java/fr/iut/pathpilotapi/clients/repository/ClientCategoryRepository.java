/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.repository;

import fr.iut.pathpilotapi.clients.ClientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientCategoryRepository extends JpaRepository<ClientCategory, Integer> {

    /**
     * Finds a ClientCategory by its name.
     *
     * @param name the name of the ClientCategory
     * @return an Optional containing the found ClientCategory, or an empty Optional if not found
     */
    Optional<ClientCategory> findByName(String name);
}
