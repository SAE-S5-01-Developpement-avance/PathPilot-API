/*
 * RouteRequestModel.java                                 30 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Route entity representing a route in order to create it")
public record RouteRequestModel(@NotNull String itineraryId) {
}
