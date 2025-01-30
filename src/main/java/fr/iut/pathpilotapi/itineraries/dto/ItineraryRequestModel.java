/*
 * ItineraryRequestModel.java                                 27 janv. 2025
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

import static fr.iut.pathpilotapi.Constants.MAX_CLIENTS;

@Getter
@Setter
@Schema(description = "Itinerary entity representing an itinerary in order to create it")
public class ItineraryRequestModel {

    @NotEmpty
    @NotNull
    @Size(max = MAX_CLIENTS)
    @Schema(description = "List of the clients to visit in the itinerary")
    private List<@NotNull Integer> clients_schedule;
}
