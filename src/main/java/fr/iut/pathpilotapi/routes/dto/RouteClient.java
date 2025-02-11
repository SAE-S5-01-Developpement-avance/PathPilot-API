/*
 * RouteClient.java                                 07 Feb 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Class representing a client in a route
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Client</li>
 *     <li>State</li>
 * </ul>
 * @see ClientDTO
 * @see ClientState
 */
@Getter
@Setter
@RequiredArgsConstructor
public class RouteClient {
    /**
     * The client
     */
    @NotNull
    @Schema(description = "The client")
    private ClientDTO client;

    /**
     * The state of the client
     */
    @NotNull
    @Schema(description = "The state of the client")
    @Enumerated(EnumType.STRING)
    private ClientState state;

    public RouteClient(ClientDTO client, ClientState state) {
        this.client = client;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteClient that = (RouteClient) o;
        return Objects.equals(getClient(), that.getClient()) && getState() == that.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClient(), getState());
    }

    @Override
    public String toString() {
        return "RouteClient{" +
                "client=" + client +
                ", state=" + state +
                '}';
    }
}