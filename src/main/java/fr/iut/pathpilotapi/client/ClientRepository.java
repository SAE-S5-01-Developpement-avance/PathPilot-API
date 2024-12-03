/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
