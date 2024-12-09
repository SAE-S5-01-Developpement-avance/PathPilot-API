/*
 * AuthenticationService.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.service;


import fr.iut.pathpilotapi.auth.dtos.LoginUserDto;
import fr.iut.pathpilotapi.auth.dtos.RegisterUserDto;
import fr.iut.pathpilotapi.auth.exceptions.EmailAlreadyTakenException;
import fr.iut.pathpilotapi.auth.exceptions.UserNotFoundException;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SalesmanRepository salesmanRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    /**
     * Create a new salesman in the database.
     *
     * @param input the user to create
     * @return the newly created salesman
     * @throws IllegalArgumentException if the email is already taken
     */
    public Salesman signup(RegisterUserDto input) {
        if (salesmanRepository.findByEmailAddress(input.getEmail()).isPresent()) {
            throw new EmailAlreadyTakenException("Email already taken");
        }
        Salesman user = new Salesman();
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setLatHomeAddress(input.getLatitude());
        user.setLongHomeAddress(input.getLongitude());
        user.setEmailAddress(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return salesmanRepository.save(user);
    }

    /**
     * Authenticate a user with its email and password.
     *
     * @param input the user to authenticate
     * @return the authenticated user
     */
    public Salesman authenticate(LoginUserDto input) {
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