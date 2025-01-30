/*
 * ClientRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.clients.dto.ClientResponseModel;
import fr.iut.pathpilotapi.clients.modelAssembler.ClientPagedModelAssembler;
import fr.iut.pathpilotapi.clients.modelAssembler.ClientResponseModelAssembler;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@Tag(name = "Client", description = "Operations related to clients")
public class ClientController {

    private final ClientResponseModelAssembler clientResponseModelAssembler;

    private final ClientService clientService;

    private final ClientPagedModelAssembler clientPagedModelAssembler;

    @Operation(
            summary = "Get all clients that belongs to the connected salesman",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of clients that belongs to the connected salesman",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping
    public ResponseEntity<PagedModel<ClientResponseModel>> getAllClientsBySalesmanPageable(Pageable pageable) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Page<Client> clients = clientService.getAllClientsBySalesmanPageable(salesman, pageable);

        if (clients.isEmpty()) {
            return ResponseEntity.ok(PagedModel.empty());
        }

        PagedModel<ClientResponseModel> pagedModel = clientPagedModelAssembler.toModel(clients);
        return ResponseEntity.ok(pagedModel);
    }


    @Operation(
            summary = "Get client with this id that belongs to the connected salesman",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The client that belongs to the connected salesman",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClientResponseModel>> getClientById(
            @Parameter(name = "id", description = "The client ID")
            @PathVariable Integer id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Client client = clientService.findByIdAndConnectedSalesman(id, salesman);

        ClientResponseModel clientRM = clientResponseModelAssembler.toModel(client);
        return ResponseEntity.ok(EntityModel.of(clientRM));
    }

    @Operation(
            summary = "Get all clients that belongs to the connected salesman",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of clients that belongs to the connected salesman",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/all")
    public CollectionModel<ClientResponseModel> getAllClientsBySalesman() {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        List<Client> clients = clientService.getAllClientsBySalesman(salesman);

        // Use the assembler to create a list of ClientResponseModel with links
        List<ClientResponseModel> responseModels = clients.stream()
                .map(clientResponseModelAssembler::toModel)
                .toList();

        // Wrap the list in a CollectionModel to add a self-link for the collection
        return CollectionModel.of(
                responseModels,
                linkTo(methodOn(ClientController.class).getAllClientsBySalesman()).withSelfRel()
        );
    }


    @Operation(
            summary = "Add a new client",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The newly created client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error"),
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<ClientResponseModel>> addClient(
            @Parameter(name = "client", description = "The newly created client")
            @RequestBody @Valid ClientRequestModel clientRM
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Client createdClient = clientService.addClient(clientRM, salesman);

        ClientResponseModel clientResponse = clientResponseModelAssembler.toModel(createdClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(clientResponse));
    }

    @Operation(
            summary = "Delete a client",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The client has been deleted"),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<Void>> deleteClient(
            @Parameter(name = "id", description = "The client ID")
            @PathVariable Integer id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        clientService.deleteByIdAndConnectedSalesman(id, salesman);

        return ResponseEntity.ok().build();
    }
}
