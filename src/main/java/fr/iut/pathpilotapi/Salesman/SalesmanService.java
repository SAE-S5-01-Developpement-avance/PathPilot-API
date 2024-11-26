package fr.iut.pathpilotapi.Salesman;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesmanService {

    /**
     * Get all salesmen from the database
     *
     * @return a list of all salesmen
     */
    public List<Salesman> getAllSalesmen() {
        List<Salesman> salesmen = new ArrayList<>();
        salesmen.add(new Salesman(1, "John", "Doe", "Azert12-", "azer@zert.fr", 12.15236, -34.15236));
        salesmen.add(new Salesman(2, "John2", "Doe", "Azert12-", "azer@zert.fr", 12.15236, -34.15236));
        return salesmen; //TODO stub
    }

    /**
     * Create a new salesman in the database
     *
     * @param salesman the salesman to create
     */
    public void addSalesman(Salesman salesman) {
        //TODO stub
    }
}
