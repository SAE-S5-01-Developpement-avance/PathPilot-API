/*
 * routeRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.CreateRouteDTO;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routes")
@Tag(name = "Route", description = "Operations related to routes")
public class RouteRestController {

    private final RouteService routeService;

    @Operation(
            summary = "Add a new route",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The newly created route",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Route.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping
    public ResponseEntity<Route> addRoute(
            @Parameter(name = "route", description = "The newly created route")
            @RequestBody CreateRouteDTO route
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();

        Route createdroute = routeService.addRoute(route, salesman);

        if (createdroute != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdroute);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get all salesman route",
            responses = {
                    @ApiResponse(responseCode = "200",
                                 description = "List of all routes from a salesman",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Route.class))),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping
    public ResponseEntity<PagedModel<Route>> getRoutesFromSalesman(
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Page<Route> routes = routeService.getAllRoutesFromSalesman(pageable, salesman.getId());
        return ResponseEntity.ok(assembler.toModel(routes));
    }

    @Operation(summary = "Delete a route",
            responses = {
                    @ApiResponse(responseCode = "200",
                                 description = "The deleted route",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Route.class))),
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(
            @Parameter(name = "id", description = "The route id")
            @PathVariable int id
    ) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        Route route = routeService.getRouteById(id);
        boolean isDeleted = routeService.delete(route, salesman);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
