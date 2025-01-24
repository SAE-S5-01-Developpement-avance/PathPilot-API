/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client.repository;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.salesman.Salesman;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Find all clients by salesman with a pageable object that specifies the page to retrieve with size and sorting.
     *
     * @param salesman the connected salesman
     * @param pageable the pageable given by the salesman
     * @return a page of clients
     */
    Page<Client> findAllBySalesman(Salesman salesman, Pageable pageable);

    /**
     * Find all clients by salesman.
     *
     * @param salesman the connected salesman
     * @return a list of clients
     */
    List<Client> findAllBySalesman(Salesman salesman);
}
