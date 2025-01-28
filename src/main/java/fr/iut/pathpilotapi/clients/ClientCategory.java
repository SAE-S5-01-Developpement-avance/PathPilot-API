/*
 * ClientType.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Class representing different category of clients.
 *
 * <p>Possible values are:
 * <ul>
 *    <li>CLIENT</li>
 *    <li>PROSPECT</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "Client category entity representing a category of client")
public class ClientCategory {

    public ClientCategory(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the clientCategory", example = "1")
    private Integer id;

    @NotNull(message = "Client category name must not be null or empty")
    @Schema(description = "Name of the client category", example = "CLIENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
