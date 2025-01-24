/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client.repository;

import fr.iut.pathpilotapi.client.ClientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCategoryRepository extends JpaRepository<ClientCategory, Integer> {
    ClientCategory findByName(String name);
}
