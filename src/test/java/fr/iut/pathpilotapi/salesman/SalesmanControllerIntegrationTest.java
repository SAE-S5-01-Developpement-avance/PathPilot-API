/*
 * SalesmanControllerIntegrationTest.java                                 17 mars 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.salesman.dto.PasswordChangeRequestModel;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class SalesmanControllerIntegrationTest {

    private static final String API_SALESMAN_URL = "/salesmen";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";
    private static final Logger log = LoggerFactory.getLogger(SalesmanControllerIntegrationTest.class);
    private static Salesman salesman;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SalesmanService salesmanService;

    @BeforeEach
    void saveSalesman() {
        salesman = IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED);
        salesmanRepository.save(salesman);
    }

    @AfterEach
    void tearDown() {
        salesmanRepository.deleteAll();
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testUpdateSalesmanPersonalInfo() throws Exception {
        // Given personal info to update
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        personalInfo.setFirstName("Updated");
        personalInfo.setLastName("Salesman");
        personalInfo.setEmailAddress("jane.doe@gmail.fr");

        // When updating the personal info
        mockMvc.perform(patch(API_SALESMAN_URL)
                        .content(IntegrationTestUtils.asJsonString(personalInfo))
                        .contentType("application/json"))
                // Then we should get a success status
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("true"));
    }


    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testUpdateSalesmanPasswordWithIncorrectOldPassword() throws Exception {
        // Given password change request with incorrect old password
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        PasswordChangeRequestModel passwordChange = new PasswordChangeRequestModel("wrongPassword", "newPassword123");

        personalInfo.setPasswordChangeRequestModel(passwordChange);

        // When updating the password
        mockMvc.perform(patch(API_SALESMAN_URL)
                        .content(IntegrationTestUtils.asJsonString(personalInfo))
                        .contentType("application/json"))
                // Then we should get an error
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testUpdateSalesmanWithInvalidEmail() throws Exception {
        // Given personal info with invalid email
        PersonalInfoRequestModel personalInfo = new PersonalInfoRequestModel();
        personalInfo.setFirstName("Updated");
        personalInfo.setLastName("Salesman");
        personalInfo.setEmailAddress("invalid-email");

        // When updating the personal info
        mockMvc.perform(patch(API_SALESMAN_URL)
                        .content(IntegrationTestUtils.asJsonString(personalInfo))
                        .contentType("application/json"))
                // Then we should get an error
                .andExpect(status().isBadRequest());
    }
}