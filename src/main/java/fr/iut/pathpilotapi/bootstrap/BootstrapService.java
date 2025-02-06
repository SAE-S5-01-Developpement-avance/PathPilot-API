/*
 * DevBootstrapService.java                                  17 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.bootstrap;

import fr.iut.pathpilotapi.clients.ClientCategory;
import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BootstrapService {

    private final Logger LOG = LoggerFactory.getLogger(BootstrapService.class);

    private final ClientCategoryRepository clientCategoryRepository;

    /**
     * Add test Entity to the Data Base
     */
    public void initialiseDevDB() {
        if (clientCategoryRepository.count() == 0) {
            LOG.info("0 Entity added to DB");
            clientCategoryRepository.save(new ClientCategory("CLIENT"));
            clientCategoryRepository.save(new ClientCategory("PROSPECT"));
        } else {
            LOG.info("Database is not empty, skipping initialization");
        }
    }
}
