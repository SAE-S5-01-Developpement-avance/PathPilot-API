/*
 * SalesmanRestController.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.routes.RouteController.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/salesmans")
@Tag(name = "Salesman", description = "Operations related to salesmen")
public class SalesmanController {

    private final SalesmanService salesmanService;

    @Operation(summary = "Modify a client's personal information",
            responses = {
                    @ApiResponse(responseCode = "400", description = "client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")})
    @PatchMapping
    public ResponseEntity<EntityModel<Status>> updatePersonalInfo(
            @Parameter(description = "The personal information to update")
            @RequestBody @Valid PersonalInfoRequestModel personalInfos
    ) {
        salesmanService.updatePersonalInfo(personalInfos);

        return ResponseEntity.ok(EntityModel.of(new Status(true)));
    }
}
