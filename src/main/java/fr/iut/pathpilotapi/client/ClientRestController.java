/*
 * ClientRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.client.dto.ClientDeleteRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.url}")
@Tag(name = "Client", description = "Operations related to clients")
public class ClientRestController {

    private final ClientService clientService;

    @Operation(
            summary = "Get all clients that belongs to the connected salesman",
            responses = {
                    @ApiResponse(
                            description = "Page of clients that belongs to the connected salesman",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Error retrieving clients")
            }
    )
    @GetMapping("/clients")
    public ResponseEntity<PagedModel<Client>> getAllClientsBySalesman(Authentication authentication,
                                                                      Pageable pageable,
                                                                      PagedResourcesAssembler assembler) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        Page<Client> client = clientService.getAllClientsBySalesman(salesman, pageable);
        return ResponseEntity.ok(assembler.toModel(client));
    }


    @Operation(
            summary = "Add a new client",
            responses = {
                    @ApiResponse(
                            description = "The newly created client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Error creating client")
            }
    )
    @PostMapping("/clients")
    public ResponseEntity<Client> addClient(
            Authentication authentication,
            @Parameter(name = "client", description = "The newly created client")
            @RequestBody Client client
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        Client createdClient = clientService.addClient(client, salesman);
        return createdClient != null ? ResponseEntity.ok(createdClient) : ResponseEntity.status(400).build();
    }


    @Operation(
            summary = "Delete a client",
            responses = {
                    @ApiResponse(
                            description = "The deleted client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Error deleting client")
            }
    )
    @DeleteMapping("/clients")
    public ResponseEntity<Client> deleteClient(
            Authentication authentication,
            @Parameter(name = "id", description = "The id of the client to delete")
            @RequestBody ClientDeleteRequestModel clientDeleteRequestModel
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        return getResponseEntityDeleteClient(clientDeleteRequestModel.getId(), salesman);
    }

    @Operation(
            summary = "Delete a client",
            responses = {
                    @ApiResponse(
                            description = "The deleted client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Error deleting client")
            }
    )
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Client> deleteClientGet(
            Authentication authentication,
            @Parameter(name = "id", description = "The client ID")
            @PathVariable int id
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        return getResponseEntityDeleteClient(id, salesman);
    }

    /**
     * Delete a client.
     * <p>
     * This method is used by both {@link #deleteClient(Authentication, ClientDeleteRequestModel)} and {@link #deleteClientGet(Authentication, int)} methods.
     *
     * @param id       the id of the client to delete
     * @param salesman the connected salesman who wants to delete the client
     * @return the response entity with the deleted client
     */
    @NotNull
    private ResponseEntity<Client> getResponseEntityDeleteClient(int id, Salesman salesman) {
        Client client = clientService.getClientById(id);

        // Check if the client belongs to the salesman, if not return 403 Unauthorized
        if (!clientService.isClientBelongToSalesman(client, salesman)) {
            throw new IllegalStateException("Client does not belong to the salesman");
        }

        return clientService.delete(client) ? ResponseEntity.ok(client) : ResponseEntity.status(404).build();
    }
}
