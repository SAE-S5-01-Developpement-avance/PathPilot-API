package fr.iut.pathpilotapi.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A salesman is defined by the following fields:
 *<p>
 * <h3>Mandatory fields</h3>
 * <ul>
 *  <li>First Name</li>
 *  <li>Last Name</li>
 *  <li>Password</li>
 *  <li>Email Address</li>
 *  <li>Home Address</li>
 *  </ul>
 * <h3>Optional fields</h3>
 * <ul>
 *  <li>Clients</li>
 *  <li>Prospects</li>
 *  <li>Routes</li>
 *  <li>Journeys</li>
 * </ul>
 */
@Entity
class Salesman {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String lastName;

    public String firstName;

    public String password;

    public String emailAddress;

    public String homeAddress;

    public Salesman(String lastName, String firstname, String password, String emailAddress, String homeAddress) {
        this.lastName = lastName;
        this.firstName = firstname;
        this.password = password;
        this.emailAddress = emailAddress;
        this.homeAddress = homeAddress;
    }

    public Salesman() {
    }
}
