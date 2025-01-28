/*
 * CreateItineraryDTO.java                                 27 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Itinerary entity representing a Itinerary in order to create it")
public class ItineraryRequestModel {

    @NotEmpty
    @NotNull
    @Size(max = 8)
    @Schema(description = "List of the clients to visit in the route")
    private List<@NotNull ClientDTO> clientsSchedule;
}
