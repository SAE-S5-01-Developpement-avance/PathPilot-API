/*
 * ClientRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.salesman.Salesman;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "Get all clients",
            responses = {
                    @ApiResponse(description = "List of all clients",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error retrieving clients")})
    @GetMapping("/clients")
    public ResponseEntity<PagedModel<Client>> getAllClients(Pageable pageable,
                                                        PagedResourcesAssembler assembler) {
        Page<Client> client = clientService.getAllClients(pageable);
        return ResponseEntity.ok(assembler.toModel(client));
    }

    @Operation(summary = "Add a new client",
            responses = {
                    @ApiResponse(description = "The newly created client",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error creating client")})
    @PostMapping("/clients")
    public ResponseEntity<Client> addClient(
            Authentication authentication,
            @Parameter(name = "client", description = "The newly created client")
            @RequestBody Client client
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        Client createdClient = clientService.addClient(client);
        return ResponseEntity.ok(createdClient);
    }

    @Operation(summary = "Delete a client",
            responses = {
                    @ApiResponse(description = "The deleted client",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error deleting client")})
    @DeleteMapping("/clients")
    public ResponseEntity<Client> deleteClient(
            @Parameter(name = "id", description = "The client ID")
            @RequestBody int id
    ) {
        Client client = clientService.getClientById(id);
        boolean isDeleted = clientService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
