/*
 * RouteRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.routes.dto.*;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routes")
@Tag(name = "Route", description = "Operations related to routes")
public class RouteController {

    private final RouteService routeService;

    private final RouteResponseModelAssembler routeResponseModelAssembler;

    private final RoutePagedModelAssembler routePagedModelAssembler;

    @Operation(
            summary = "Create a new route",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The newly created route",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Route.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<RouteResponseModel>> createRoute(
            @Parameter(name = "itineraryId", description = "The itinerary id to create the route")
            @RequestBody @Valid RouteRequestModel itineraryId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Route createdroute = routeService.createRoute(itineraryId.itineraryId(), salesman);
        RouteResponseModel routeResponseModel = routeResponseModelAssembler.toModel(createdroute);

        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(routeResponseModel));
    }

    @Operation(
            summary = "Starts a new route",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The route has been set with a start date, the salesman current position and its state to IN_PROGRESS"),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PatchMapping("/{id}/start")
    public ResponseEntity<EntityModel<Status>> startRoute(
            @Parameter(name = "id", description = "The route id to start the route")
            @PathVariable String id,

            @Parameter(name = "geoCord", description = "The current position of the salesman")
            @RequestBody @Valid GeoCord geoCord
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.startRoute(id, geoCord, salesman);

        EntityModel<Status> statusModel = EntityModel.of(new Status(true));
        statusModel.add(
                linkTo(methodOn(RouteController.class).startRoute(id, null))
                        .withSelfRel()
                        //Add info that endpoint should be called with a requestBody
                        .andAffordance(afford(methodOn(RouteController.class).startRoute(id, geoCord)))
                ).add(
                        linkTo(
                                methodOn(RouteController.class).stopRoute(id)
                        ).withRel("stop")
                ).add(
                        linkTo(
                                methodOn(RouteController.class).pauseRoute(id)
                        ).withRel("pause")
        );
        return ResponseEntity.ok(statusModel);
    }

    @Operation(
            summary = "Resume a route",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The route has been set with the salesman current position and its state to IN_PROGRESS"),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PatchMapping("/{id}/resume")
    public ResponseEntity<EntityModel<Status>> resumeRoute(
            @Parameter(name = "id", description = "The route id to resume the route")
            @PathVariable String id,

            @Parameter(name = "geoCord", description = "The current position of the salesman")
            @RequestBody @Valid GeoCord geoCord
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.resumeRoute(id, geoCord, salesman);

        EntityModel<Status> statusModel = EntityModel.of(new Status(true));
        statusModel.add(
                linkTo(methodOn(RouteController.class).resumeRoute(id, null))
                        .withSelfRel()
                        //Add info that endpoint should be called with a requestBody
                        .andAffordance(afford(methodOn(RouteController.class).resumeRoute(id, geoCord)))
        ).add(
                linkTo(
                        methodOn(RouteController.class).pauseRoute(id)
                ).withRel("pause")
        ).add(
                linkTo(
                        methodOn(RouteController.class).stopRoute(id)
                ).withRel("stop")
        );
        return ResponseEntity.ok(statusModel);
    }

    @Operation(
            summary = "Completely stops a route",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The route has been updated with the state to STOPPED"
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PatchMapping("/{id}/stop")
    public ResponseEntity<EntityModel<Status>> stopRoute(
            @Parameter(name = "id", description = "The route id to stop the route")
            @PathVariable String id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.stopRoute(id, salesman);

        EntityModel<Status> statusModel = EntityModel.of(new Status(true));
        statusModel.add(
                linkTo(
                        methodOn(RouteController.class).stopRoute(id)
                ).withSelfRel()
        );
        return ResponseEntity.ok(statusModel);
    }

    @Operation(
            summary = "Pause a route",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The route has been updated with the state to PAUSED"
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PatchMapping("/{id}/pause")
    public ResponseEntity<EntityModel<Status>> pauseRoute(
            @Parameter(name = "id", description = "The route id to pause the route")
            @PathVariable String id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.pauseRoute(id, salesman);

        EntityModel<Status> statusModel = EntityModel.of(new Status(true));
        statusModel.add(
                linkTo(
                        methodOn(RouteController.class).pauseRoute(id)
                ).withSelfRel()
        ).add(
                linkTo(
                        methodOn(RouteController.class).stopRoute(id)
                ).withSelfRel()
        ).add(
                linkTo(methodOn(RouteController.class).resumeRoute(id, null))
                        .withSelfRel()
                        //Add info that endpoint should be called with a requestBody
                        .andAffordance(afford(methodOn(RouteController.class).resumeRoute(id, new GeoCord(0.0, 0.0))))
        );
        return ResponseEntity.ok(statusModel);
    }


    @Operation(
            summary = "Get a route",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The wanted route",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Route.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @GetMapping("/{routeId}")
    public ResponseEntity<EntityModel<RouteResponseModel>> getRoute(
            @Parameter(name = "routeId", description = "The wanted route id")
            @PathVariable String routeId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Route createdroute = routeService.findByIdAndConnectedSalesman(routeId, salesman);
        RouteResponseModel routeResponseModel = routeResponseModelAssembler.toModel(createdroute);

        return ResponseEntity.ok(EntityModel.of(routeResponseModel));
    }

    @Operation(
            summary = "Get all salesman routes",
            responses = {
                    @ApiResponse(responseCode = "200",
                                 description = "Page of all routes from a salesman",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Route.class))),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping
    public ResponseEntity<PagedModel<RouteResponseModel>> getRoutesFromSalesman(Pageable pageable) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Page<Route> routes = routeService.getAllRoutesFromSalesman(salesman, pageable);

        if (routes.isEmpty()) {
            return ResponseEntity.ok(PagedModel.empty());
        }

        PagedModel<RouteResponseModel> pagedModel = routePagedModelAssembler.toModel(routes);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Delete a route",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The route has been deleted"),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/{routeId}")
    public ResponseEntity<Status> deleteRoute(
            @Parameter(name = "routeId", description = "The route id")
            @PathVariable String routeId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.deleteByIdAndConnectedSalesman(routeId, salesman);

        return ResponseEntity.ok(new Status(true));
    }

    @Operation(summary = "Set a client as visited in a route",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The client has been set as visited"),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/{routeId}/clients/{clientId}/visited")
    public ResponseEntity<Status> setClientVisited(
            @Parameter(name = "routeId", description = "The route id")
            @PathVariable String routeId,
            @Parameter(name = "clientId", description = "The client id")
            @PathVariable Integer clientId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.setClientVisited(clientId, routeId, salesman);

        return ResponseEntity.ok(new Status(true));
    }

    @Operation(summary = "Set a client as skipped in a route",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The client has been set as skipped"),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/{routeId}/clients/{clientId}/skipped")
    public ResponseEntity<Status> setClientSkipped(
            @Parameter(name = "routeId", description = "The route id")
            @PathVariable String routeId,
            @Parameter(name = "clientId", description = "The client id")
            @PathVariable Integer clientId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.setClientSkipped(clientId, routeId, salesman);

        return ResponseEntity.ok(new Status(true));
    }



    private record Status (boolean state) {}
}