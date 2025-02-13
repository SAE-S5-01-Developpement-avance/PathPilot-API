package fr.iut.pathpilotapi;

import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    private static final String EMAIL_SALESMAN_CONNECTED = "test@test.fr";
    private static final String PASSWORD_SALESMAN_CONNECTED = "123456789";

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeTestExecution
    void saveSalesman() {
        salesmanRepository.save(
                IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED)
        );
    }

    //Unauthorized status
    @Test
    void testGetBadCredentialsException() throws Exception {
        //Login DTO with wrong email
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("example@test.fr", "123456789");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").exists());
    }

    // Forbidden status
    @Test
    void testAccountStatusException() throws Exception {
        mockMvc.perform(get("/account-locked"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccessDeniedException() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSignatureException() throws Exception {
        mockMvc.perform(get("/invalid-signature"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testExpiredJwtException() throws Exception {
        mockMvc.perform(get("/expired-jwt"))
                .andExpect(status().isForbidden());
    }

    // Conflict status
    @Test
    void testEmailAlreadyTakenException() throws Exception {
        mockMvc.perform(get("/email-taken"))
                .andExpect(status().isConflict());
    }

    // NotFound status
    @Test
    void testUserNotFoundException() throws Exception {
        mockMvc.perform(get("/user-not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObjectNotFoundException() throws Exception {
        mockMvc.perform(get("/non-existent-endpoint"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNoResourceFoundException() throws Exception {
        mockMvc.perform(get("/no-resource"))
                .andExpect(status().isNotFound());
    }

    // BadRequest status
    @Test
    void testMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(get("/invalid-argument"))
                .andExpect(status().isBadRequest());
    }

    // Internal server status for general exception
    @Test
    void testGenericException() throws Exception {
        mockMvc.perform(get("/generic-error"))
                .andExpect(status().isInternalServerError());
    }
}