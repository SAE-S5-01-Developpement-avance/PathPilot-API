/*
 * AuthenticationControllerIntegrationTest.java                                 03 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.auth;

import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {

    private static final String API_AUTH_URL = "/auth";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;

    @BeforeTestExecution
    void saveSalesman() {
        salesmanRepository.save(
                IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED)
        );
    }

    @Test
    void testRegisterUser() throws Exception {
        RegisterUserRequestModel registerUser = IntegrationTestUtils.createRegisterUserRequestModel();

        mockMvc.perform(post(API_AUTH_URL + "/signup")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(registerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", Matchers.is(registerUser.getEmail())));
    }

    @Test
    void testRegisterUserWithInvalidEmail() throws Exception {
        RegisterUserRequestModel registerUser = IntegrationTestUtils.createRegisterUserRequestModel();
        registerUser.setEmail("invalid-email");

        mockMvc.perform(post(API_AUTH_URL + "/signup")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(registerUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUserWithShortPassword() throws Exception {
        RegisterUserRequestModel registerUser = IntegrationTestUtils.createRegisterUserRequestModel();
        registerUser.setPassword("short");

        mockMvc.perform(post(API_AUTH_URL + "/signup")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(registerUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser() throws Exception {
        Salesman salesman = IntegrationTestUtils.createSalesman("example@example.fr", IntegrationTestUtils.encodePassword("123456789"));
        salesmanRepository.save(salesman);
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("example@example.fr", "123456789");

        mockMvc.perform(post(API_AUTH_URL + "/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testAuthenticateUserWithInvalidEmail() throws Exception {
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("invalid-email", "123456789");

        mockMvc.perform(post(API_AUTH_URL + "/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUserWithShortPassword() throws Exception {
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("example@example.fr", "12");
        loginUser.setPassword("short");

        mockMvc.perform(post(API_AUTH_URL + "/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                .andExpect(status().isBadRequest());
    }
}
