/*
 * SalesmanRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesmanRepository extends JpaRepository<Salesman, Integer> {
    /**
     * Finds a Salesman entity by its email address.
     *
     * @param email the email address of the salesman
     * @return an Optional containing the Salesman entity if found, or empty if not found
     */
    Optional<Salesman> findByEmailAddress(String email);

    /**
     * Checks if a Salesman entity exists by its email address.
     *
     * @param email the email address to check
     * @return true if a Salesman entity with the given email address exists, false otherwise
     */
    boolean existsByEmailAddress(String email);
}
