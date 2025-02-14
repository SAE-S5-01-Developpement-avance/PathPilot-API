package fr.iut.pathpilotapi;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Class to represent a geographical coordinate.
 * @param latitude the latitude of the point
 * @param longitude the longitude of the point
 */
public record GeoCord(@Min(value = -90, message = "Latitude must be between -90 and 90")
                      @Max(value = 90, message = "Latitude must be between -90 and 90")
                      Double latitude,
                      @Min(value = -180, message = "Longitude must be between -180 and 180")
                      @Max(value = 180, message = "Longitude must be between -180 and 180")
                      Double longitude) {
}
