/*
 * SalesmanRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.Salesman.controller;

import fr.iut.pathpilotapi.Salesman.models.Salesman;
import fr.iut.pathpilotapi.Salesman.service.SalesmanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.url}")
@Tag(name = "Salesman", description = "Operations related to salesmen")
public class SalesmanRestController {

    private final SalesmanService salesmanService;

    @Operation(summary = "Get all salesmen",
            responses = {
                    @ApiResponse(description = "List of all salesmen",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Salesman.class))),
                    @ApiResponse(responseCode = "400", description = "Error retrieving salesmen")})
    @GetMapping("/salesmen")
    public List<Salesman> list() {
        return salesmanService.getAllSalesmen();
    }

    @Operation(summary = "Add a new salesman",
            responses = {
                    @ApiResponse(description = "The newly created salesman",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Salesman.class))),
                    @ApiResponse(responseCode = "400", description = "Error creating salesman")})
    @PostMapping("/salesmen")
    public Salesman addSalesman(
            @Parameter(name = "salesman", description = "The newly created salesman" )
            @RequestBody Salesman salesman
    ) {
        return salesmanService.addSalesman(salesman);
    }

    @PostMapping("/login")
    public boolean login(String email, String password) {
        return true;
    }
}
