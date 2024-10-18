/*
 * DevBootstrap.java                                  17 oct. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.bootstrap;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Handle different action that needs to be executed during the launch of the server
 * @author François de Saint Palais
 */
@Component
public class DevBootstrap {

    private BootstrapService bootstrapService;

    public DevBootstrap(
            BootstrapService bootstrapService
    ) {
        this.bootstrapService = bootstrapService;
    }

    /**
     * All the action that going to be executed during the server's startup
     */
    @PostConstruct
    private void init() {
        bootstrapService.initialiseDevDB();
    }
}
