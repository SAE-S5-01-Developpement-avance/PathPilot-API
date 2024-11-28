/*
 * ClientRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Page<Client> getAllClients(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return clientService.getAllClients(PageRequest.of(page, size));
    }

    @Operation(summary = "Add a new client",
            responses = {
                    @ApiResponse(description = "The newly created client",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error creating client")})
    @PostMapping("/clients")
    public Client addClient(
            @Parameter(name = "client", description = "The newly created client" )
            @RequestBody Client client
    ) {
        return clientService.addClient(client);
    }

    @Operation(summary = "Delete a client",
            responses = {
                    @ApiResponse(description = "The deleted client",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error deleting client")})
    @DeleteMapping("/clients")
    public boolean addClient(
            @Parameter(name = "id", description = "The  client ID" )
            @RequestBody int id
    ) {
        return clientService.deleteById(id);
    }
}
