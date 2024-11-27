/*
 * DevBootstrapService.java                                  17 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class BootstrapService {

    private final Logger LOG = LoggerFactory.getLogger(BootstrapService.class);

    /**
     * Add test Entity to the Data Base
     */
    public void initialiseDevDB() {
        //TODO add the test Entity to the DB
        LOG.info("O Entity added to DB");
    }
}
