/*
 * Client.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.salesman.Salesman;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

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
    @Schema(description = "Unique identifier of the client", example = "1")
    private Integer id;

    @NotNull(message = "Company name must not be null or empty")
    @Schema(description = "Name of the company", example = "IKEA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @NotNull(message = "Latitude must not be null or empty")
    @Schema(description = "Latitude of the company's location", example = "48.8566", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Schema(description = "Longitude of the company's location", example = "2.3522", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longHomeAddress;

    @ManyToOne
    @JoinColumn(name = "client_category_id")
    @Schema(description = "Type of the client", example = "CLIENT")
    private ClientCategory clientCategory;

    @Size(max = 1000)
    @Schema(description = "Description of the client", example = "Description A")
    private String description;

    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the contact person", example = "Doe")
    private String contactLastName;

    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the contact person", example = "John")
    private String contactFirstName;

    @Pattern(regexp = "(\\d{10})|()")
    @Schema(description = "Phone number of the contact person", example = "0123456789")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    private Salesman salesman;
}