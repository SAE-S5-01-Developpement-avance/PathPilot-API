/*
 * SalesmanRepository.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesmanRepository extends JpaRepository<Salesman, Integer> {
    Optional<Salesman> findByEmailAddress(String email);
}
