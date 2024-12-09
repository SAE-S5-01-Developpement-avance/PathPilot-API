package fr.iut.pathpilotapi.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ClientCategoryRepositoryIntegrationTest {

    @Autowired
    private ClientCategoryRepository clientCategoryRepository;

    @Test
    void testFindByName() {
        // Given a client category in the database
        ClientCategory clientCategory = new ClientCategory();
        clientCategory.setName("Test");
        clientCategoryRepository.save(clientCategory);

        // When we're finding the client category by name
        ClientCategory foundClientCategory = clientCategoryRepository.findByName("Test");

        // Then the client category should be found
        assertEquals(clientCategory, foundClientCategory, "The client category should be the one in the database");
    }

    @Test
    void testFindByNameNotFound() {
        // Given a client category in the database
        ClientCategory clientCategory = new ClientCategory();
        clientCategory.setName("Test");
        clientCategoryRepository.save(clientCategory);

        // When we're finding the client category by name
        ClientCategory foundClientCategory = clientCategoryRepository.findByName("Not found");

        // Then the client category should not be found
        assertNull(foundClientCategory, "The client category should not be found");
    }
}