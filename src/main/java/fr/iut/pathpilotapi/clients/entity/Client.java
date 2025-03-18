/*
 * Client.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.entity;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.security.SecurityUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Company Name</li>
 *     <li>GPS coordinates (where is its company)</li>
 *      <ul>
 *          <li>Latitude</li>
 *          <li>Longitude</li>
 *      </ul>
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
@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "Client entity representing a client or prospect")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String companyName;

    private double latHomeAddress;

    private double longHomeAddress;

    @ManyToOne
    @JoinColumn(name = "client_category_id")
    private ClientCategory clientCategory;

    private String description;

    private String contactLastName;

    private String contactFirstName;

    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    private Salesman salesman;

    /**
     * Create a new client from a client request model
     *
     * @param clientRequestModel the client request model
     */
    public Client(ClientRequestModel clientRequestModel) {
        this.companyName = clientRequestModel.getCompanyName();
        this.latHomeAddress = clientRequestModel.getLatHomeAddress();
        this.longHomeAddress = clientRequestModel.getLongHomeAddress();
        this.clientCategory = new ClientCategory(clientRequestModel.getClientCategory());
        this.description = clientRequestModel.getDescription();
        this.contactLastName = clientRequestModel.getContactLastName();
        this.contactFirstName = clientRequestModel.getContactFirstName();
        this.phoneNumber = clientRequestModel.getPhoneNumber();
        this.salesman = SecurityUtils.getCurrentSalesman();
    }

    public void setGeoCord(GeoCord geoCord) {
        this.latHomeAddress = geoCord.latitude();
        this.longHomeAddress = geoCord.longitude();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", latHomeAddress=" + latHomeAddress +
                ", longHomeAddress=" + longHomeAddress +
                ", clientCategory=" + clientCategory +
                ", description='" + description + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", salesman=" + salesman +
                '}';
    }
}