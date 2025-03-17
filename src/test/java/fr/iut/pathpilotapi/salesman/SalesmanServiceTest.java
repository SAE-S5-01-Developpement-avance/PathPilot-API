/*
 * SalesmanServiceTest.java                                 17 mars 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.exceptions.EmailAlreadyTakenException;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.exceptions.SalesmanBelongingException;
import fr.iut.pathpilotapi.salesman.dto.PasswordChangeRequestModel;
import fr.iut.pathpilotapi.security.SecurityUtils;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SalesmanServiceTest {

    @Mock
    private SalesmanRepository salesmanRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SalesmanService salesmanService;

    private Salesman testSalesman;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSalesman = IntegrationTestUtils.createSalesman();
        testSalesman.setId(1);
        testSalesman.setEmailAddress("test@example.com");
        testSalesman.setPassword("encodedPassword");
    }

    @Test
    void testSignUp_Success() {
        // Given
        RegisterUserRequestModel request = new RegisterUserRequestModel();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");
        request.setLatitude(45.0);
        request.setLongitude(2.0);

        when(salesmanRepository.findByEmailAddress(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(salesmanRepository.save(any(Salesman.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Salesman result = salesmanService.signUp(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getFirstName(), result.getFirstName());
        assertEquals(request.getLastName(), result.getLastName());
        assertEquals(request.getEmail(), result.getEmailAddress());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(request.getLatitude(), result.getLatHomeAddress());
        assertEquals(request.getLongitude(), result.getLongHomeAddress());

        verify(passwordEncoder).encode(request.getPassword());
        verify(salesmanRepository).save(any(Salesman.class));
    }

    @Test
    void testSignUp_EmailAlreadyTaken() {
        // Given
        RegisterUserRequestModel request = new RegisterUserRequestModel();
        request.setEmail("existing@example.com");

        when(salesmanRepository.existsByEmailAddress(request.getEmail())).thenReturn(true);

        // When/Then
        Exception exception = assertThrows(EmailAlreadyTakenException.class, () -> {
            salesmanService.signUp(request);
        });

        assertEquals("Email already taken", exception.getMessage());
        verify(salesmanRepository).existsByEmailAddress(request.getEmail());
        verify(salesmanRepository, never()).save(any(Salesman.class));
    }

    @Test
    void testFindById_Success() {
        // Given
        Integer id = 1;
        when(salesmanRepository.findById(id)).thenReturn(Optional.of(testSalesman));

        // When
        Salesman result = salesmanService.findById(id, testSalesman);

        // Then
        assertEquals(testSalesman, result);
        verify(salesmanRepository).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Integer id = 999;
        when(salesmanRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            salesmanService.findById(id, testSalesman);
        });

        assertEquals("Salesman with id 999 not found", exception.getMessage());
        verify(salesmanRepository).findById(id);
    }

    @Test
    void testFindById_NotBelongingToSalesman() {
        // Given
        Integer id = 2;
        Salesman otherSalesman = new Salesman();
        otherSalesman.setEmailAddress("other@example.com");

        when(salesmanRepository.findById(id)).thenReturn(Optional.of(otherSalesman));

        // When/Then
        Exception exception = assertThrows(SalesmanBelongingException.class, () -> {
            salesmanService.findById(id, testSalesman);
        });

        assertEquals("The salesman information queried does not belong to the connected salesman.", exception.getMessage());
        verify(salesmanRepository).findById(id);
    }

    @Test
    void testUpdatePersonalInfo_AllFields() {
        // Given
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        personalInfo.setFirstName("NewFirstName");
        personalInfo.setLastName("NewLastName");
        personalInfo.setEmailAddress("new@example.com");

        PasswordChangeRequestModel passwordChange = new PasswordChangeRequestModel(
                "oldPassword", "newPassword"
        );
        personalInfo.setPasswordChangeRequestModel(passwordChange);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentSalesman).thenReturn(testSalesman);

            when(salesmanRepository.findByEmailAddress("new@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.matches("oldPassword", testSalesman.getPassword())).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

            // When
            salesmanService.updatePersonalInfo(personalInfo);

            // Then
            assertEquals("NewFirstName", testSalesman.getFirstName());
            assertEquals("NewLastName", testSalesman.getLastName());
            assertEquals("new@example.com", testSalesman.getEmailAddress());
            assertEquals("newEncodedPassword", testSalesman.getPassword());

            verify(salesmanRepository).findByEmailAddress("new@example.com");
            verify(passwordEncoder).matches("oldPassword", "encodedPassword");
            verify(passwordEncoder).encode("newPassword");
        }
    }

    @Test
    void testUpdatePersonalInfo_EmailAlreadyTaken() {
        // Given
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        personalInfo.setEmailAddress("existing@example.com");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentSalesman).thenReturn(testSalesman);

            when(salesmanRepository.findByEmailAddress("existing@example.com")).thenReturn(Optional.of(new Salesman()));

            // When/Then
            Exception exception = assertThrows(EmailAlreadyTakenException.class, () -> {
                salesmanService.updatePersonalInfo(personalInfo);
            });

            assertEquals("Email existing@example.com already taken", exception.getMessage());
            verify(salesmanRepository).findByEmailAddress("existing@example.com");
        }
    }

    @Test
    void testUpdatePersonalInfo_IncorrectPassword() {
        // Given
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        PasswordChangeRequestModel passwordChange = new PasswordChangeRequestModel(
                "wrongPassword", "newPassword"
        );
        personalInfo.setPasswordChangeRequestModel(passwordChange);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentSalesman).thenReturn(testSalesman);

            when(passwordEncoder.matches("wrongPassword", testSalesman.getPassword())).thenReturn(false);

            // When/Then
            Exception exception = assertThrows(AccessDeniedException.class, () -> {
                salesmanService.updatePersonalInfo(personalInfo);
            });

            assertEquals("Invalid password provided; Please type your former password.", exception.getMessage());
            verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
            verify(passwordEncoder, never()).encode(anyString());
        }
    }
}
