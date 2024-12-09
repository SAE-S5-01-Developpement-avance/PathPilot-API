/*
 * ClientDeleteRequestModel.java                                 06 déc. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Model for the request to delete a client.
 */
@Getter
@Setter
public class ClientDeleteRequestModel {
    private int id;
}