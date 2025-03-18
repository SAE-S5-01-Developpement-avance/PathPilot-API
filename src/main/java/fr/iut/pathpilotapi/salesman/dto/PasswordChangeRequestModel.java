/*
 * PasswordChangeRequestModel.java                                 10 mars 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman.dto;

/**
 * RequestModel for the password change
 *
 * @param formerPassword the former password to write in order to certificate the identity
 * @param newPassword    the new password to set
 */
public record PasswordChangeRequestModel(String formerPassword, String newPassword) {
}
