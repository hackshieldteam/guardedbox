package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTION_PUBLIC_KEY_LENGTH;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: Account.
 * Contains the following fields: accountId, email, encryptionPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
public class AccountWithEncryptionPublicKeyEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -7091538371782098518L;

    /** Account ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    @Positive
    private Long accountId;

    /** Email. */
    @Column(name = "email")
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Encryption Public Key. */
    @Column(name = "encryption_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = ENCRYPTION_PUBLIC_KEY_LENGTH, max = ENCRYPTION_PUBLIC_KEY_LENGTH)
    private String encryptionPublicKey;

    /**
     * Default Constructor.
     */
    public AccountWithEncryptionPublicKeyEntity() {}

    /**
     * Constructor with Attributes.
     *
     * @param accountId AccountId.
     */
    public AccountWithEncryptionPublicKeyEntity(
            Long accountId) {
        this.accountId = accountId;
    }

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

}
