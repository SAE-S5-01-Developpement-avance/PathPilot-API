/*
 * ClientRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.salesman.Salesman;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Find all clients by salesman with a pageable object that specifies the page to retrieve with size and sorting.
     *
     * @param salesman the connected salesman
     * @param pageable the pageable given by the salesman
     * @return a page of clients
     */
    Page<Client> findAllBySalesman(Salesman salesman, Pageable pageable);
}
