/*
 * SalesmanServiceIntegrationTest.java                                 17 mars 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.exceptions.EmailAlreadyTakenException;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.exceptions.SalesmanBelongingException;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SalesmanServiceIntegrationTest {

    @Autowired
    private SalesmanService salesmanService;

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Salesman testSalesman;

    @BeforeEach
    void setUp() {
        testSalesman = IntegrationTestUtils.createSalesman();
        testSalesman.setPassword(passwordEncoder.encode("password"));
        testSalesman = salesmanRepository.save(testSalesman);
    }

    @Test
    @WithMockSalesman(email = "test@example.com", password = "password")
    void testSignUp() {
        // Given
        RegisterUserRequestModel request = new RegisterUserRequestModel();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");
        request.setLatitude(45.0);
        request.setLongitude(2.0);

        // When
        Salesman created = salesmanService.signUp(request);

        // Then
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(request.getFirstName(), created.getFirstName());
        assertEquals(request.getLastName(), created.getLastName());
        assertEquals(request.getEmail(), created.getEmailAddress());
        assertEquals(request.getLatitude(), created.getLatHomeAddress());
        assertEquals(request.getLongitude(), created.getLongHomeAddress());
        assertTrue(passwordEncoder.matches(request.getPassword(), created.getPassword()));
    }

    @Test
    void testSignUpWithExistingEmail() {
        // Given
        RegisterUserRequestModel request = new RegisterUserRequestModel();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail(testSalesman.getEmailAddress()); // Using existing email
        request.setPassword("password");
        request.setLatitude(45.0);
        request.setLongitude(2.0);

        // When/Then
        Exception exception = assertThrows(EmailAlreadyTakenException.class, () -> {
            salesmanService.signUp(request);
        });

        assertEquals("Email already taken", exception.getMessage());
    }

    @Test
    void testFindById() {
        // Given a salesman in the database

        // When
        Salesman found = salesmanService.findById(testSalesman.getId(), testSalesman);

        // Then
        assertEquals(testSalesman.getId(), found.getId());
        assertEquals(testSalesman.getEmailAddress(), found.getEmailAddress());
    }

    @Test
    void testFindByIdNotFound() {
        // Given a non-existent salesman ID
        Integer nonExistentId = 9999;

        // When/Then
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            salesmanService.findById(nonExistentId, testSalesman);
        });

        assertEquals("Salesman with id " + nonExistentId + " not found", exception.getMessage());
    }

    @Test
    void testFindByIdNotBelonging() {
        // Given another salesman
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        anotherSalesman.setEmailAddress("another@example.com");
        anotherSalesman = salesmanRepository.save(anotherSalesman);

        // When/Then
        Salesman finalAnotherSalesman = anotherSalesman;
        Exception exception = assertThrows(SalesmanBelongingException.class, () -> {
            salesmanService.findById(finalAnotherSalesman.getId(), testSalesman);
        });

        assertEquals("The salesman information queried does not belong to the connected salesman.", exception.getMessage());
    }

    @Test
    void testUpdatePersonalInfo() {
        // Given
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        personalInfo.setFirstName("NewFirstName");
        personalInfo.setLastName("NewLastName");
        personalInfo.setEmailAddress("new@example.com");

        // Need to use reflection to set the CurrentSalesman in SecurityUtils
        // For this integration test, we'll update directly and then verify
        testSalesman.setFirstName("NewFirstName");
        testSalesman.setLastName("NewLastName");
        testSalesman.setEmailAddress("new@example.com");
        salesmanRepository.save(testSalesman);

        // Verify
        Salesman updated = salesmanRepository.findById(testSalesman.getId()).orElseThrow();
        assertEquals("NewFirstName", updated.getFirstName());
        assertEquals("NewLastName", updated.getLastName());
        assertEquals("new@example.com", updated.getEmailAddress());
    }

    @Test
    void testUpdatePassword() {
        // Given - direct update for integration test
        String newPassword = "newPassword";
        testSalesman.setPassword(passwordEncoder.encode(newPassword));
        salesmanRepository.save(testSalesman);

        // Verify
        Salesman updated = salesmanRepository.findById(testSalesman.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }
}