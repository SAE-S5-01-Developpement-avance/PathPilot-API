/*
 * AuthenticationService.java                                  28 nov. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.auth.service;


import fr.iut.pathpilotapi.auth.dtos.LoginUserDto;
import fr.iut.pathpilotapi.auth.dtos.RegisterUserDto;
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


    public Salesman signup(RegisterUserDto input) {
        Salesman user = new Salesman();
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setLatHomeAddress(input.getLatitude());
        user.setLongHomeAddress(input.getLongitude());
        user.setEmailAddress(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return salesmanRepository.save(user);
    }

    public Salesman authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return salesmanRepository.findByEmailAddress(input.getEmail())
                .orElseThrow();
    }
}