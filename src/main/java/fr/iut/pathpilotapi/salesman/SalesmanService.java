/*
 * SalesmanService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesmanService {

    private final SalesmanRepository salesmanRepository;

    /**
     * Get all salesmen from the database
     *
     * @return a list of all salesmen
     */
    public List<Salesman> getAllSalesmen() {
       return salesmanRepository.findAll();
    }

    /**
     * Create a new salesman in the database.
     *
     * @param salesman the salesman to create
     * @return the newly created salesman
     */
    public Salesman addSalesman(Salesman salesman) {
        return salesmanRepository.save(salesman);
    }

    /**
     * Get a salesman by its id.
     *
     * @param id the id of the salesman
     * @return the salesman
     * @throws IllegalArgumentException if the salesman is not found
     */
    public Salesman getSalesmanById(Integer id) {
        return salesmanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Salesman not found"));
    }
}
