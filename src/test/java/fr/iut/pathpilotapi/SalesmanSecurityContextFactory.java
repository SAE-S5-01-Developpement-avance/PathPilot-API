/*
 * SalesmanSecurityContextFactory.java                                  09 déc. 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SalesmanSecurityContextFactory implements WithSecurityContextFactory<WithMockSalesman> {


    private final SalesmanRepository salesmanRepository;

    public SalesmanSecurityContextFactory(SalesmanRepository salesmanRepository) {
        this.salesmanRepository = salesmanRepository;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockSalesman annotation) {
        // Créer ou récupérer un Salesman
        Salesman salesman = salesmanRepository.findByEmailAddress(annotation.email())
            .orElseGet(() -> {
                Salesman newSalesman = IntegrationTestUtils.createSalesman(annotation.email(), annotation.password());
                return salesmanRepository.save(newSalesman);
            });

        // Créer les autorités
        List<GrantedAuthority> authorities = Arrays.stream(annotation.roles())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        // Créer l'authentification
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            salesman,
            annotation.password(),
            authorities
        );

        // Créer et configurer le contexte de sécurité
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}