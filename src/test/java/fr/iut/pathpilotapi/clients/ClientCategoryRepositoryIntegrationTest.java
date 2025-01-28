package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ClientCategoryRepositoryIntegrationTest {

    @Autowired
    private ClientCategoryRepository clientCategoryRepository;

    private final String clientName = "Test";

    @Test
    void testFindByName() {
        // Given a client category in the database
        ClientCategory clientCategory = new ClientCategory();
        clientCategory.setName(clientName);
        clientCategoryRepository.save(clientCategory);

        // When we're finding the client category by name
        ClientCategory foundClientCategory = clientCategoryRepository.findByName(clientName);

        // Then the client category should be found
        assertEquals(clientCategory, foundClientCategory, "The client category should be the one in the database");
    }

    @Test
    void testFindByNameNotFound() {
        // Given a client category in the database
        ClientCategory clientCategory = new ClientCategory();
        clientCategory.setName(clientName);
        clientCategoryRepository.save(clientCategory);

        // When we're finding the client category by name
        String notClientName = "Not found";
        assertNotEquals(notClientName, clientName, "The client name and the name searched should not be the same");
        ClientCategory foundClientCategory = clientCategoryRepository.findByName(notClientName);

        // Then the client category should not be found
        assertNull(foundClientCategory, "The client category should not be found");
    }
}