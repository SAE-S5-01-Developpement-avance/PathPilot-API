/*
 * RouteStartRequestModel.java                                 13 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.GeoCord;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "Route entity representing a route in order to start it")
public record RouteStartRequestModel (String routeId, @Valid GeoCord currentPosition) {
}
