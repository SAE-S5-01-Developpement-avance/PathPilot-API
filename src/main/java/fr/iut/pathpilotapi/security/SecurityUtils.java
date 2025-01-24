/*
 * SecurityUtils.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.security;

import fr.iut.pathpilotapi.salesman.Salesman;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Salesman getCurrentSalesman() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Salesman salesman) {
            return salesman;
        }

        throw new IllegalStateException("No authenticated salesman found");
    }
}
