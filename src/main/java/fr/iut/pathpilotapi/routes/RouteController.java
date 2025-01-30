/*
 * RouteRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.RoutePagedModelAssembler;
import fr.iut.pathpilotapi.routes.dto.RouteResponseModel;
import fr.iut.pathpilotapi.routes.dto.RouteResponseModelAssembler;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody String itineraryId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Route createdroute = routeService.createRoute(itineraryId, salesman);
        RouteResponseModel routeResponseModel = routeResponseModelAssembler.toModel(createdroute);

        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(routeResponseModel));
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
            return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> deleteRoute(
            @Parameter(name = "routeId", description = "The route id")
            @PathVariable String routeId
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        routeService.deleteByIdAndConnectedSalesman(routeId, salesman);

        return ResponseEntity.ok().build();
    }
}
