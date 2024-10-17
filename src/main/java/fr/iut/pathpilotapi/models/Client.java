package fr.iut.pathpilotapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Company Name</li>
 *     <li>GPS coordinates (where is its company)</li>
 * </ul>
 * <h3>Optional fields</h3>
 * <ul>
 *     <li>Client Type</li>
 *     <ul>
 *         <li>Client</li>
 *         <li>Prospect</li>
 *     </ul>
 *     <li>Description</li>
 *     <li>Contact Last Name</li>
 *     <li>Contact First Name</li>
 *     <li>Phone number</li>
 */
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String companyName;

    public String gpsCoordinates;

    public ClientType clientType;

    public String description;

    public String contactLastName;

    public String contactFirstName;

    public String phoneNumber;

    //Only mandatory parameters
    public Client(String companyName, String gpsCoordinates) {
        this.companyName = companyName;
        this.gpsCoordinates = gpsCoordinates;
    }

    public Client() {
    }
}

