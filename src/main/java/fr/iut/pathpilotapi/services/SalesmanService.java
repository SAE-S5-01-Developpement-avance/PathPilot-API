package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.models.Salesman;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesmanService {

    /**
     * Get all salesmen from the database
     * @return a list of all salesmen
     */
    public List<Salesman> getAllSalesmen() {
        List<Salesman> salesmen = new ArrayList<>();
        salesmen.add(new Salesman("John", "Doe", "Azert12-", "azer@zert.fr", "12.15236","-34.15236"));
        salesmen.add(new Salesman("John2", "Doe", "Azert12-", "azer@zert.fr", "12.15236","-34.15236"));
        return salesmen; //TODO stub
    }

    /**
     * Create a new salesman in the database
     * @param salesman the salesman to create
     */
    public void addSalesman(Salesman salesman) {
        //TODO stub
    }

    public boolean checkPassword(String candidate) {
        // Check that an unencrypted password matches one that has
        // previously been hashed
        //TODO get the hashed version from database
        String hashed = null;
        if (BCrypt.checkpw(candidate, hashed)) {
            System.out.println("It matches");
            return true;
        } else {
            System.out.println("It does not match");
            return false;
        }
    }


}
