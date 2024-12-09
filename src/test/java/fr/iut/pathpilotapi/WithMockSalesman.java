package fr.iut.pathpilotapi;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

/**
 * Annotation to be used in tests to mock a salesman.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = SalesmanSecurityContextFactory.class)
public @interface WithMockSalesman {
    String email() default "test@example.com";

    String password() default "password";

    String[] roles() default {"ROLE_SALESMAN"};
}