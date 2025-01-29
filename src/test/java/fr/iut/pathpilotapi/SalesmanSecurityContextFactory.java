/*
 * SalesmanSecurityContextFactory.java                                  09 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi;

import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating a {@link SecurityContext} with a mocked salesman.
 * <p>
 *     It's a necessary class to mock a connexion with a salesman in tests.
 * </p>
 * @see WithMockSalesman
 */
public class SalesmanSecurityContextFactory implements WithSecurityContextFactory<WithMockSalesman> {

    private final SalesmanRepository salesmanRepository;

    public SalesmanSecurityContextFactory(SalesmanRepository salesmanRepository) {
        this.salesmanRepository = salesmanRepository;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockSalesman annotation) {
        // Create or retrieve the salesman
        Salesman salesman = salesmanRepository.findByEmailAddress(annotation.email())
                .orElseGet(() -> {
                    Salesman newSalesman = IntegrationTestUtils.createSalesman(annotation.email(), annotation.password());
                    return salesmanRepository.save(newSalesman);
                });

        // Handle roles
        List<GrantedAuthority> authorities = Arrays.stream(annotation.roles())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Handle authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                salesman,
                annotation.password(),
                authorities
        );

        // Create and configure the security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}