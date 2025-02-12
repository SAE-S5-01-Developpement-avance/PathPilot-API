/*
 * MongoClient.java                                 12 Feb 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * Class representing a client in MongoDB
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "lite_clients")
public class MongoClient {

    @Id
    private Integer id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    public MongoClient(Integer id, double latitude, double longitude) {
        this.id = id;
        this.location = new GeoJsonPoint(longitude, latitude);
    }
}