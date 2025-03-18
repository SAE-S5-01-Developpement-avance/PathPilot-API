/*
 * SalesmanService.java                                 03 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.exceptions.EmailAlreadyTakenException;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.exceptions.SalesmanBelongingException;
import fr.iut.pathpilotapi.salesman.dto.PasswordChangeRequestModel;
import fr.iut.pathpilotapi.security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
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
     * @throws EmailAlreadyTakenException if the email is already taken
     */
    public Salesman signUp(RegisterUserRequestModel input) {
        if (salesmanRepository.existsByEmailAddress(input.getEmail())) {
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

    public Salesman findByEmailAddress(@Email String email) {
        return salesmanRepository.findByEmailAddress(email).orElseThrow(
                () -> new ObjectNotFoundException("Salesman not found with email: " + email)
        );
    }

    /**
     * Find the {@link Salesman} by the given id
     *
     * @param id                salesman's id to find
     * @param salesmanConnected the connected salesman
     * @return the salesman found
     */
    public Salesman findById(Integer id, Salesman salesmanConnected) {

        Salesman salesmanFound = salesmanRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Salesman with id %d not found".formatted(id))
        );
        if (!salesmanFound.getEmailAddress().equals(salesmanConnected.getEmailAddress())) {
            throw new SalesmanBelongingException("The salesman information queried does not belong to the connected salesman.");
        }
        return salesmanFound;
    }

    /**
     * Update the personal information of the connected salesman
     *
     * @param personalInfos the personal information to update
     */
    public void updatePersonalInfo(@Valid PersonalInfoRequestModel personalInfos) {
        Salesman salesman = SecurityUtils.getCurrentSalesman();
        if (StringUtils.isNotBlank(personalInfos.getLastName())) {
            this.changeLastName(personalInfos.getLastName(), salesman);
        }
        if (StringUtils.isNotBlank(personalInfos.getFirstName())) {
            this.changeFirstName(personalInfos.getFirstName(), salesman);
        }
        if (StringUtils.isNotBlank(personalInfos.getEmailAddress())) {
            this.changeEmailAddress(personalInfos.getEmailAddress(), salesman);
        }
        if (personalInfos.getPasswordChangeRequestModel() != null) {
            this.changePassword(personalInfos.getPasswordChangeRequestModel(), salesman);
        }
        salesmanRepository.save(salesman);
    }


    /**
     * Change salesman's lastname
     *
     * @param lastName new salesman's lastname
     * @param salesman connected
     */
    private void changeLastName(String lastName, Salesman salesman) {
        salesman.setLastName(lastName);
    }

    /**
     * Change salesman's firstname
     *
     * @param firstName new salesman's firstname
     * @param salesman  connected
     */
    private void changeFirstName(String firstName, Salesman salesman) {
        salesman.setFirstName(firstName);
    }

    /**
     * Change the email address of the salesman only if the new email address is not already taken
     *
     * @param emailAddress the new email address
     * @param salesman     the connected Salesman who changes his email address
     * @throws EmailAlreadyTakenException if the email is already taken
     */
    private void changeEmailAddress(String emailAddress, Salesman salesman) {
        if (salesmanRepository.findByEmailAddress(emailAddress).isPresent()) {
            throw new EmailAlreadyTakenException("Email %s already taken".formatted(emailAddress));
        }
        salesman.setEmailAddress(emailAddress);
    }

    /**
     * Change the password of the salesman only if former password and the actual one in db matches, else throw an exception
     *
     * @param passwordChangeRM RequestModel containing information to change the password
     * @param salesman         the connected Salesman who changes his password
     * @throws AccessDeniedException if the former password is not correct
     */
    private void changePassword(PasswordChangeRequestModel passwordChangeRM, Salesman salesman) {
        if (!passwordEncoder.matches(passwordChangeRM.formerPassword(), salesman.getPassword())) {
            throw new AccessDeniedException("Invalid password provided; Please type your former password.");
        }
        salesman.setPassword(passwordEncoder.encode(passwordChangeRM.newPassword()));
        log.debug("Password changed for salesman with email {}", salesman.getEmailAddress());
    }
}
