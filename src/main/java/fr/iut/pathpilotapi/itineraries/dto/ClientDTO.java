/*
 * ClientDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.dto;

import fr.iut.pathpilotapi.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Objects;

/**
 * DTO representing a client in MongoDb
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ClientDTO {

    @Id
    @NotNull
    @Schema(description = "client's Id")
    private int id;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Schema(description = "position of the client's company")
    private GeoJsonPoint companyLocation;

    @NotNull
    @NotEmpty
    @Schema(description = "name of the client's company")
    private String companyName;

    public static ClientDTO createFromClient(Client client) {
        ClientDTO clientDTO = new ClientDTO();

        clientDTO.setId(client.getId());
        clientDTO.setCompanyName(client.getCompanyName());
        clientDTO.setCompanyLocation(
                new GeoJsonPoint(client.getLongHomeAddress(), client.getLatHomeAddress())
        );

        return clientDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return getId() == clientDTO.getId()
                && Objects.equals(getCompanyLocation(), clientDTO.getCompanyLocation())
                && Objects.equals(getCompanyName(), clientDTO.getCompanyName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCompanyLocation(), getCompanyName());
    }
}
