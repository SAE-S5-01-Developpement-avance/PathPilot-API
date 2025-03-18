/*
 * ItineraryRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.Status;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryPagedModelAssembler;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryResponseModel;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryResponseModelAssembler;
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
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/itineraries")
@Slf4j
@Tag(name = "Itinerary", description = "Operations related to itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    private final ItineraryResponseModelAssembler itineraryResponseModelAssembler;

    private final ItineraryPagedModelAssembler itineraryPagedModelAssembler;

    private final ClientRepository clientRepository;

    @Operation(
            summary = "Add a new itinerary",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The newly created itinerary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Itinerary.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<ItineraryResponseModel>> addItinerary(
            @Parameter(name = "itinerary", description = "The itinerary information needed to create one")
            @RequestBody @Valid ItineraryRequestModel itinerary
    ) {
        log.info("Creating itinerary with clients: {}", itinerary.getClients_schedule());
        List<Client> clients = clientRepository.findAllById(itinerary.getClients_schedule());

        Salesman salesman = SecurityUtils.getCurrentSalesman();

        List<List<Double>> matrixDistances = itineraryService.getDistances(clients, "driving-car", salesman).block();
        Itinerary createdItinerary = itineraryService.createItinerary(itinerary, salesman, matrixDistances);
        ItineraryResponseModel itineraryResponseModel = itineraryResponseModelAssembler.toModel(createdItinerary);

        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(itineraryResponseModel));
    }

    @Operation(
            summary = "Get an itinerary",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The wanted itinerary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Itinerary.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/{itineraryId}")
    public ResponseEntity<EntityModel<ItineraryResponseModel>> getItinerary(
            @Parameter(name = "itineraryId", description = "The wanted itinerary id")
            @PathVariable String itineraryId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Itinerary itinerary = itineraryService.findByIdAndConnectedSalesman(itineraryId, salesman);
        ItineraryResponseModel itineraryResponseModel = itineraryResponseModelAssembler.toModel(itinerary);

        return ResponseEntity.ok(EntityModel.of(itineraryResponseModel));
    }

    @Operation(summary = "Get a page of itineraries",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of all itineraries from a salesman",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Itinerary.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping
    public ResponseEntity<PagedModel<ItineraryResponseModel>> getItinerariesFromSalesman(Pageable pageable) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Page<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesmanPageable(salesman, pageable);

        if (itineraries.isEmpty()) {
            return ResponseEntity.ok(PagedModel.empty());
        }

        PagedModel<ItineraryResponseModel> pagedModel = itineraryPagedModelAssembler.toModel(itineraries);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Delete a itinerary",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The itinerary has been deleted"),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<Status> deleteItinerary(
            @Parameter(name = "itineraryId", description = "The Itinerary id")
            @PathVariable String itineraryId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        itineraryService.deleteByIdAndConnectedSalesman(itineraryId, salesman);

        return ResponseEntity.ok(new Status(true));
    }

    @Operation(
            summary = "Get all itineraries",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The wanted itineraries",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Itinerary.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/all")
    public CollectionModel<ItineraryResponseModel> getAllItinerariesBySalesman() {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        List<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesman(salesman);

        List<ItineraryResponseModel> responseModels = itineraries.stream()
                .map(itineraryResponseModelAssembler::toModel).toList();
        return CollectionModel.of(responseModels,
                linkTo(methodOn(ItineraryController.class).getAllItinerariesBySalesman()).withSelfRel());
    }
}
