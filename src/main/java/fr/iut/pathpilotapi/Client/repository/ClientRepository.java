/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.Client.repository;

import fr.iut.pathpilotapi.Client.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
