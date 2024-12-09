/*
 * ClientRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.client.Client;
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
@Tag(name = "Route", description = "Operations related to routes")
public class RouteRestController {

    private final RouteService routeService;

    @Operation(summary = "Get all salesman route",
            responses = {
                    @ApiResponse(description = "List of all routes from a salesman",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class))),
                    @ApiResponse(responseCode = "400", description = "Error retrieving routes")})
    @GetMapping("/routes/{salesman_id}")
    public ResponseEntity<PagedModel<Route>> getRoutesFromSalesman(Pageable pageable,
                                                        PagedResourcesAssembler assembler, @PathVariable("salesman_id") int salesmanId) {
        Page<Route> routes = routeService.getAllRoutesFromSalesman(pageable, salesmanId);
        return ResponseEntity.ok(assembler.toModel(routes));
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
        Client createdClient = routeService.addClient(client);
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
        Client client = routeService.getClientById(id);
        boolean isDeleted = routeService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
