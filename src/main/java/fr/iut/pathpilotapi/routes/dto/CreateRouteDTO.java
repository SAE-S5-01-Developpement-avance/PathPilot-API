/*
 * CreateRouteDTO.java                                  13 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.routes.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO to create a route
 * A route is just a list of clients to visit
 * Every other information is calculated by the system
 */
@Getter
@Setter
public class CreateRouteDTO {
    private List<Integer> clients_schedule;
}
