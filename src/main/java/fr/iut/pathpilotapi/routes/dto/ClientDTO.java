/*
 * ClientDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * DTO representing a client in MongoDb
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ClientDTO {

    @NotNull
    @Schema(description = "client's Id")
    private Integer client;

    @NotNull
    @Schema(description = "position of the client's company")
    private PositionDTO companyLocation;

    @NotNull
    @NotEmpty
    @Schema(description = "name of the client's company")
    private String companyName;

    public static ClientDTO createFromClient(Client client) {
        ClientDTO clientDTO = new ClientDTO();

        clientDTO.setClient(client.getId());
        clientDTO.setCompanyName(client.getCompanyName());
        clientDTO.setCompanyLocation(
                new PositionDTO(client.getLatHomeAddress(), client.getLongHomeAddress())
        );

        return clientDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientDTO clientDTO = (ClientDTO) o;
        return     Objects.equals(client, clientDTO.client)
                && Objects.equals(companyLocation, clientDTO.companyLocation)
                && Objects.equals(companyName, clientDTO.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, companyLocation, companyName);
    }
}
