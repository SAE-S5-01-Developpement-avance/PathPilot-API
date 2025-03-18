/*
 * AuthenticationService.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.service;


import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.exceptions.UserNotFoundException;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SalesmanRepository salesmanRepository;

    private final AuthenticationManager authenticationManager;

    /**
     * Authenticate a user with its email and password.
     *
     * @param input the user to authenticate
     * @return the authenticated user
     */
    public Salesman authenticate(LoginUserRequestModel input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return salesmanRepository.findByEmailAddress(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}