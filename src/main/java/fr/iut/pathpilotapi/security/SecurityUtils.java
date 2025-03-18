/*
 * SecurityUtils.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.security;

import fr.iut.pathpilotapi.salesman.Salesman;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Retrieves the currently authenticated Salesman.
     *
     * @return the currently authenticated Salesman
     * @throws IllegalStateException if no authenticated Salesman is found
     */
    public static Salesman getCurrentSalesman() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Salesman salesman))
            throw new IllegalStateException("No authenticated salesman found");

        return salesman;
    }
}
