/*
 * LoginResponseModel.java                                 03 févr. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.auth.dto;

import lombok.Getter;

/**
 * Response object for login endpoint
 */
@Getter
public class LoginResponseModel {
    private String token;
    private long expiresIn;

    public LoginResponseModel setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponseModel setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
