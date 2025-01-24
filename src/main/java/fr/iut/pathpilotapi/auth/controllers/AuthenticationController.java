/*
 * AuthenticationController.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.controllers;

import fr.iut.pathpilotapi.auth.dtos.LoginUserDto;
import fr.iut.pathpilotapi.auth.dtos.RegisterUserDto;
import fr.iut.pathpilotapi.auth.service.AuthenticationService;
import fr.iut.pathpilotapi.auth.service.JwtService;
import fr.iut.pathpilotapi.salesman.Salesman;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication")
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;


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
    public ResponseEntity<Salesman> register(@RequestBody RegisterUserDto registerUserDto) {
        Salesman registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(
            summary = "Authenticate a user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The token and its expiration time",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Client error"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Salesman authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Response object for login endpoint
     */
    @Getter
    public static class LoginResponse {
        private String token;
        private long expiresIn;

        public LoginResponse setToken(String token) {
            this.token = token;
            return this;
        }

        public LoginResponse setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
    }
}