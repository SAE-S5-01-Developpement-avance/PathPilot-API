/*
 * ClientDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.dto;

import fr.iut.pathpilotapi.clients.entity.Client;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * DTO representing a client in MongoDB
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ClientDTO {

    @NotNull
    @Schema(description = "client's Id")
    private Integer id;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Schema(description = "position of the client's company")
    private GeoJsonPoint companyLocation;

    @NotNull
    @NotEmpty
    @Schema(description = "name of the client's company")
    private String companyName;

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.companyLocation = new GeoJsonPoint(client.getLongHomeAddress(), client.getLatHomeAddress());
        this.companyName = client.getCompanyName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return Objects.equals(getId(), clientDTO.getId())
                && Objects.equals(getCompanyLocation(), clientDTO.getCompanyLocation())
                && Objects.equals(getCompanyName(), clientDTO.getCompanyName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCompanyLocation(), getCompanyName());
    }
}
