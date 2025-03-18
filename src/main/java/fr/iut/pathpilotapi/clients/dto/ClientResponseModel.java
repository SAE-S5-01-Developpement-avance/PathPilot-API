/*
 * Client.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.dto;

import fr.iut.pathpilotapi.clients.entity.ClientCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

/**
 * Client entity representing a client or prospect.
 */
@Getter
@Setter
@Schema(description = "Client entity representing a client or prospect")
public class ClientResponseModel extends RepresentationModel<ClientResponseModel> {

    @Schema(description = "Unique identifier of the client", example = "1")
    private Integer id;

    @Schema(description = "Name of the company", example = "IKEA")
    private String companyName;

    @Schema(description = "Latitude of the company's location", example = "48.8566")
    private double latHomeAddress;

    @Schema(description = "Longitude of the company's location", example = "2.3522")
    private double longHomeAddress;

    @Schema(description = "Type of the client", example = "CLIENT")
    private ClientCategory clientCategory;

    @Schema(description = "Description of the client", example = "Description A")
    private String description;

    @Schema(description = "Last name of the contact person", example = "Doe")
    private String contactLastName;

    @Schema(description = "First name of the contact person", example = "John")
    private String contactFirstName;

    @Schema(description = "Phone number of the contact person", example = "0123456789")
    private String phoneNumber;
}