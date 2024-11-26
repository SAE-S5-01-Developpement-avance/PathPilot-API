package fr.iut.pathpilotapi.Client.models;

import fr.iut.pathpilotapi.Client.enums.ClientType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class Client {

    /**
     * Max length authorized for lastName, firstName
     */
    private final int MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "Company name must not be null or empty")
    private String companyName;

    @NotNull(message = "Latitude must not be null or empty")
    private String latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    private String longHomeAddress;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    private String contactLastName;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    private String contactFirstName;

    @Pattern(regexp = "[0-9]{10}]")
    private String phoneNumber;
}

