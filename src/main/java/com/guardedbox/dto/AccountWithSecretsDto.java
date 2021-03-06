package com.guardedbox.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account with Secrets associated to it.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithSecretsDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 8481536477701691288L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Encryption Public Key. */
    private String encryptionPublicKey;

    /** Secrets. */
    private List<SecretDto> secrets;

    /**
     * @return The accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(
            Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(
            String email) {
        this.email = email;
    }

    /**
     * @return The encryptionPublicKey.
     */
    public String getEncryptionPublicKey() {
        return encryptionPublicKey;
    }

    /**
     * @param encryptionPublicKey The encryptionPublicKey to set.
     */
    public void setEncryptionPublicKey(
            String encryptionPublicKey) {
        this.encryptionPublicKey = encryptionPublicKey;
    }

    /**
     * @return The secrets.
     */
    public List<SecretDto> getSecrets() {
        return secrets;
    }

    /**
     * @param secrets The secrets to set.
     */
    public void setSecrets(
            List<SecretDto> secrets) {
        this.secrets = secrets;
    }

}
