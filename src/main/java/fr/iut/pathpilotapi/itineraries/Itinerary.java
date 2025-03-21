/*
 * Itinerary.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

/**
 * Class representing an itinerary
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Salesman ID</li>
 *     <li>Home position of the salesman</li>
 *     <li>Clients schedule</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "itinerary")
public class Itinerary {

    @Id
    private String id;

    /**
     * The salesman ID (keep it in camelCase, else SpringFramework will not be able to map the field correctly)
     */
    private Integer salesmanId;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_home;

    private List<@NotNull ClientDTO> clients_schedule;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Itinerary itinerary = (Itinerary) o;
        return Objects.equals(salesmanId, itinerary.salesmanId)
                && Objects.equals(id, itinerary.id)
                && Objects.equals(salesman_home, itinerary.salesman_home)
                && Objects.equals(clients_schedule, itinerary.clients_schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesmanId, salesman_home, clients_schedule);
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "id='" + id + '\'' +
                ", salesman=" + salesmanId +
                ", salesmanHome=" + salesman_home +
                ", clients_schedule=" + clients_schedule +
                '}';
    }
}
