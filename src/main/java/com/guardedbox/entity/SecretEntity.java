package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_NAME_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "secret")
public class SecretEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 6161942842947773439L;

    /** Secret ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "secret_id")
    @Positive
    private Long secretId;

    /** Owner Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_account_id")
    @NotNull
    @Valid
    private AccountEntity ownerAccount;

    /** Name. */
    @Column(name = "name")
    @NotBlank
    @Size(max = SECRET_NAME_MAX_LENGTH)
    private String name;

    /** Value. */
    @Column(name = "value")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /** Shared Secrets Based on this Secret. */
    @OneToMany(mappedBy = "secret", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedSecretEntity> sharedSecrets;

    /**
     * @return The secretId.
     */
    public Long getSecretId() {
        return secretId;
    }

    /**
     * @param secretId The secretId to set.
     */
    public void setSecretId(
            Long secretId) {
        this.secretId = secretId;
    }

    /**
     * @return The ownerAccount.
     */
    public AccountEntity getOwnerAccount() {
        return ownerAccount;
    }

    /**
     * @param ownerAccount The ownerAccount to set.
     */
    public void setOwnerAccount(
            AccountEntity ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(
            String name) {
        this.name = name;
    }

    /**
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(
            String value) {
        this.value = value;
    }

    /**
     * @return The sharedSecrets.
     */
    public List<SharedSecretEntity> getSharedSecrets() {
        return sharedSecrets;
    }

    /**
     * @param sharedSecrets The sharedSecrets to set.
     */
    public void setSharedSecrets(
            List<SharedSecretEntity> sharedSecrets) {
        this.sharedSecrets = sharedSecrets;
    }

}
