/*
 * SalesmanService.java                                 03 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.exceptions.EmailAlreadyTakenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesmanService {

    private final SalesmanRepository salesmanRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new salesman in the database.
     *
     * @param input the user to create
     * @return the newly created salesman
     * @throws IllegalArgumentException if the email is already taken
     */
    public Salesman signUp(RegisterUserRequestModel input) {
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
}
