/*
 * SalesmanRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Salesman> getAllSalesmen() {
        return salesmanService.getAllSalesmen();
    }
}
