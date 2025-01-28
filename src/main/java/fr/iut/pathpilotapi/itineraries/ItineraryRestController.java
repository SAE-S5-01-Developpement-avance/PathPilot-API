/*
 * routeRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryResponseModel;
import fr.iut.pathpilotapi.itineraries.modelAssembler.ItineraryResponseModelAssembler;
import fr.iut.pathpilotapi.itineraries.routes.Route;
import fr.iut.pathpilotapi.itineraries.routes.dto.RouteResponseModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.security.SecurityUtils;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itineraries")
@Tag(name = "Itinerary", description = "Operations related to itineraries")
public class ItineraryRestController {

    private final ItineraryService itineraryService;

    private final ItineraryResponseModelAssembler itineraryResponseModelAssembler;

    @Operation(
            summary = "Add a new itinerary",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The newly created itinerary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Itinerary.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<ItineraryResponseModel>> addItinerary(
            @Parameter(name = "itinerary", description = "The newly created itinerary")
            @RequestBody ItineraryRequestModel itinerary
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Itinerary createdItinerary = itineraryService.addItinerary(itinerary, salesman);

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
                                    schema = @Schema(implementation = Route.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/{itineraryId}")
    public ResponseEntity<EntityModel<ItineraryResponseModel>> getRoute(
            @Parameter(name = "itineraryId", description = "The wanted itinerary id")
            @PathVariable String itineraryId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Itinerary itinerary = itineraryService.findByIdAndConnectedSalesman(itineraryId, salesman);
        ItineraryResponseModel itineraryResponseModel = itineraryResponseModelAssembler.toModel(itinerary);

        return ResponseEntity.ok(EntityModel.of(itineraryResponseModel));
    }

    @Operation(summary = "Get all itineraries",
            responses = {
                    @ApiResponse(responseCode = "200",
                                 description = "List of all itineraries from a salesman",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Itinerary.class))),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping
    public ResponseEntity<PagedModel<Itinerary>> getItinerariesFromSalesman(
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Page<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesman(salesman, pageable);
        return ResponseEntity.ok(assembler.toModel(itineraries));
    }

    @Operation(summary = "Delete a itinerary",
            responses = {
                    @ApiResponse(responseCode = "200",
                                 description = "The deleted itinerary",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Itinerary.class))),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(
            @Parameter(name = "id", description = "The Itinerary id")
            @PathVariable String id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Itinerary itinerary = itineraryService.addItinerary(id);
        boolean isDeleted = itineraryService.delete(itinerary, salesman);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
