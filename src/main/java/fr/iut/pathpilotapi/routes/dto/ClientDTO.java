/*
 * ClientDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import com.mongodb.client.model.geojson.Position;
import fr.iut.pathpilotapi.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
public class ClientDTO {

    @NotNull
    @Schema(description = "client's Id")
    private Integer client;

    @NotNull
    @Schema(description = "position of the client's company")
    private PositionDTO company_location;

    @NotNull
    @NotEmpty
    @Schema(description = "name of the client's company")
    private String company_name;

    public static ClientDTO createFromClient(Client client) {
        ClientDTO clientDTO = new ClientDTO();

        clientDTO.setClient(client.getId());
        clientDTO.setCompany_name(client.getCompanyName());
        clientDTO.setCompany_location(
                new PositionDTO(client.getLatHomeAddress(), client.getLongHomeAddress())
        );

        return clientDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return     Objects.equals(client, clientDTO.client)
                && Objects.equals(company_location, clientDTO.company_location)
                && Objects.equals(company_name, clientDTO.company_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, company_location, company_name);
    }
}
