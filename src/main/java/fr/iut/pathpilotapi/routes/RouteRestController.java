/*
 * routeRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.dto.DeleteRequestModel;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.url}")
@Tag(name = "Route", description = "Operations related to routes")
public class RouteRestController {

    private final RouteService routeService;

    @Operation(
            summary = "Add a new route",
            responses = {
                    @ApiResponse(
                            description = "The newly created route",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Route.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Error creating route"),
                    @ApiResponse(responseCode = "201", description = "Succesfully created route"),
            }
    )
    @PostMapping("/routes")
    public ResponseEntity<Route> addRoute(
            Authentication authentication,
            @Parameter(name = "route", description = "The newly created route")
            @RequestBody Route route
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        route.setSalesman(salesman.getId());
        Route createdroute = routeService.addRoute(route, salesman);
        if (createdroute != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdroute);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get all salesman route",
            responses = {
                    @ApiResponse(description = "List of all routes from a salesman",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Route.class))),
                    @ApiResponse(responseCode = "400", description = "Error retrieving routes"),
                    @ApiResponse(responseCode = "200", description = "successfull operation")})
    @GetMapping("/routes")
    public ResponseEntity<PagedModel<Route>> getRoutesFromSalesman(
            Authentication authentication,
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        Page<Route> routes = routeService.getAllRoutesFromSalesman(pageable, salesman.getId());
        return ResponseEntity.ok(assembler.toModel(routes));
    }

    @Operation(summary = "Delete a route",
            responses = {
                    @ApiResponse(description = "The deleted route",
                                 content = @Content(mediaType = "application/json",
                                 schema = @Schema(implementation = Route.class))),
                    @ApiResponse(responseCode = "404", description = "The route does not exists"),
                    @ApiResponse(responseCode = "200", description = "route deleted")})
    @DeleteMapping("/routes")
    public ResponseEntity<Void> deleteRoute(
            @Parameter(name = "id", description = "The route id")
            @RequestBody DeleteRequestModel requestModel,
            Authentication authentication
    ) {
        Salesman salesman = (Salesman) authentication.getPrincipal();
        Route route = routeService.getRouteById(requestModel.getId());
        boolean isDeleted = routeService.delete(route, salesman);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
