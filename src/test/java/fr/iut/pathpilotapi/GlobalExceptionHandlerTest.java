package fr.iut.pathpilotapi;

import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.auth.dto.RegisterUserRequestModel;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Key;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Autowired
    private ClientRepository clientRepository;

    @BeforeTestExecution
    void saveSalesman() {
        salesmanRepository.save(
                IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED)
        );
    }

    //Unauthorized status
    @Test
    void testGetBadCredentialsException_InvalidEmail() throws Exception {
        //Given a user with bad credentials
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("example@test.fr", "123456789");

        // when we try to login with the wrong credentials
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                // then we should get an unauthorized status
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBadCredentialsException_InvalidPassword() throws Exception {
        //Given a user with bad credentials
        LoginUserRequestModel loginUser = IntegrationTestUtils.createLoginUserRequestModel("test@test.fr", "123wrongpassword");

        // when we try to login with the wrong credentials
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(loginUser)))
                // then we should get an unauthorized status
                .andExpect(status().isUnauthorized());
    }

    // Forbidden status
    @Test
    void testAccessDeniedException() throws Exception {
        //Given a client created on another salesman
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman("another@salesman.fr", "12345678");
        salesmanRepository.save(anotherSalesman);
        Client client = IntegrationTestUtils.createClient(anotherSalesman);
        client.setId(1);
        clientRepository.save(client);

        // when we try to access the clients endpoint with the wrong salesman
        mockMvc.perform(get("/client/{id}", 1))
                // then we should get a forbidden status
                .andExpect(status().isForbidden());
    }

    @Test
    void testSignatureException() throws Exception {
        // Given an invalid token that we corrupt on purpose
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String validToken = Jwts.builder()
                .setSubject("user")
                .signWith(key)
                .compact();

        // Corrupt the token by modifying its payload
        String corruptedToken = validToken.substring(0, validToken.length() - 2) + "xx";

        System.out.println("Valid Token: " + validToken);
        System.out.println("Corrupted Token: " + corruptedToken);

        // when we try to access an endpoint with the corrupted token
        mockMvc.perform(get("/clients")
                        .header("Authorization", "Bearer " + corruptedToken))
                // then we should get a forbidden status
                .andExpect(status().isForbidden());
    }

    @Test
    void testExpiredJwtException() throws Exception {
        // Given an expired token
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String expiredToken = Jwts.builder()
                .setSubject("user")
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        // when we try to access an endpoint with the expired token
        mockMvc.perform(get("/clients")
                        .header("Authorization", "Bearer " + expiredToken))
                // then we should get a forbidden status
                .andExpect(status().isForbidden());
    }

    // Conflict status
    @Test
    void testEmailAlreadyTakenException() throws Exception {
        // Given a request using an already taken email
        RegisterUserRequestModel registerUser = IntegrationTestUtils.createRegisterUserRequestModel("test@test.fr", "12345678");

        // when we try to signup with the wrong credentials
        mockMvc.perform(post("/auth/signup")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(registerUser)))
                // then we should get a conflict status
                .andExpect(status().isConflict());
    }

    // NotFound status
    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testObjectNotFoundException() throws Exception {
        //given an object that does not exist, take a client as test
        int id = 1;

        // when we try to access the client endpoint with the wrong id
        mockMvc.perform(get("/client/{id}", id))
                // then we should get a not found status
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testNoResourceFoundException() throws Exception {
        // Given a request to a non-existing endpoint
        String path = "/non-existing-endpoint";

        // when we try to access the non-existing endpoint
        mockMvc.perform(get(path))
                // then we should get a not found status
                .andExpect(status().isNotFound());
    }

    // BadRequest status
    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testMethodArgumentNotValidException() throws Exception {
        // Given a request with invalid parameters
        ClientRequestModel clientRequestModel = new ClientRequestModel();
        clientRequestModel.setContactFirstName("John");
        clientRequestModel.setContactLastName("Doe");
        clientRequestModel.setDescription("john.doe");

        // when we try to access the client endpoint with the wrong id
        mockMvc.perform(post("/clients")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRequestModel)))
                // then we should get a bad request status
                .andExpect(status().isBadRequest());
    }
}