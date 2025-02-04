/*
 * AuthenticationController.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.controllers;

import fr.iut.pathpilotapi.auth.dto.LoginResponseModel;
import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.auth.service.AuthenticationService;
import fr.iut.pathpilotapi.auth.service.JwtService;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanService;
import fr.iut.pathpilotapi.salesman.dto.SalesmanResponseModel;
import fr.iut.pathpilotapi.salesman.dto.SalesmanResponseModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication")
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final SalesmanService salesmanService;

    private final SalesmanResponseModelAssembler salesmanResponseModelAssembler;

    @Operation(
            summary = "Register a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The newly created user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Salesman.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping("/signup")
        public ResponseEntity<EntityModel<SalesmanResponseModel>> register(@RequestBody @Valid RegisterUserRequestModel registerUserRequestModel) {
        Salesman registeredUser = salesmanService.signUp(registerUserRequestModel);
        SalesmanResponseModel salesmanResponseModel = salesmanResponseModelAssembler.toModel(registeredUser);
        return ResponseEntity.ok(EntityModel.of(salesmanResponseModel));
    }

    @Operation(
            summary = "Authenticate a user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The token and its expiration time",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseModel.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<EntityModel<LoginResponseModel>> authenticate(@RequestBody @Valid LoginUserRequestModel loginUserRequestModel) {
        Salesman authenticatedUser = authenticationService.authenticate(loginUserRequestModel);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseModel loginResponse = new LoginResponseModel().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        // Wrap the repsponseModel in a EntityModel to add a self-link for the repsponseModel
        return ResponseEntity.ok(EntityModel.of(
                loginResponse,
                linkTo(methodOn(AuthenticationController.class).authenticate(loginUserRequestModel)).withSelfRel()));
    }
}