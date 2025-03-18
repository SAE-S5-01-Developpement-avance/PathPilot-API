/*
 * MongoClient.java                                 12 Feb 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * Class representing a client in MongoDB
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "mongoClient")
public class MongoClient {

    @Id
    private Integer id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    /**
     * The category of the client.
     * This class client is always a prospect.
     */
    private ClientCategory category = ClientCategory.PROSPECT;

    public MongoClient(Integer id, double latitude, double longitude) {
        this.id = id;
        this.location = new GeoJsonPoint(longitude, latitude);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MongoClient that = (MongoClient) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getLocation(), that.getLocation()) && Objects.equals(getCategory().getName(), that.getCategory().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLocation(), getCategory());
    }
}