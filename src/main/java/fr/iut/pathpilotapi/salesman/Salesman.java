/*
 * Salesman.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

/**
 * A salesman is defined by the following fields:
 * <p>
 * <h3>Mandatory fields</h3>
 * <ul>
 *  <li>First Name</li>
 *  <li>Last Name</li>
 *  <li>Password</li>
 *  <li>Email Address</li>
 *  <li>Home Address</li>
 *  </ul>
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Salesman entity representing a salesman")
public class Salesman implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the salesman", example = "1")
    private Integer id;

    @NotNull(message = "Lastname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Last name of the salesman", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotNull(message = "Firstname must not be null or empty")
    @Size(max = MAX_LENGTH)
    @Schema(description = "First name of the salesman", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /**
     * Hashed password, treated in service
     */
    @NotNull(message = "Password must not be null or empty")
    @Schema(description = "Hashed password of the salesman", example = "hashedpassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotNull(message = "Email must not be null or empty")
    @Email(message = "Email must be valid: example@example.fr")
    @Size(max = MAX_LENGTH)
    @Schema(description = "Email address of the salesman", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailAddress;

    @NotNull(message = "Latitude must not be null or empty")
    @Schema(description = "Latitude of the salesman's home address", example = "48.8566", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latHomeAddress;

    @NotNull(message = "Longitude must not be null or empty")
    @Schema(description = "Longitude of the salesman's home address", example = "2.3522", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longHomeAddress;

    // Override methods from UserDetails

    /**
     * @return Get the authorities of the {@link Salesman}, i.e., the roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return """
                Salesman{
                    id=%d,
                    lastName='%s',
                    firstName='%s',
                    password='%s',
                    emailAddress='%s',
                    latHomeAddress=%f,
                    longHomeAddress=%f
                }
                """.formatted(id, lastName, firstName, password, emailAddress, latHomeAddress, longHomeAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Salesman salesman = (Salesman) o;

        if (Double.compare(salesman.latHomeAddress, latHomeAddress) != 0) return false;
        if (Double.compare(salesman.longHomeAddress, longHomeAddress) != 0) return false;
        if (!id.equals(salesman.id)) return false;
        if (!lastName.equals(salesman.lastName)) return false;
        if (!firstName.equals(salesman.firstName)) return false;

        return true;
    }
}