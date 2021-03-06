package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;

/**
 * Repository: Account.
 * Entity: AccountWithEncryptionPublicKeyEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithEncryptionPublicKeyEntitiesRepository
        extends JpaRepository<AccountWithEncryptionPublicKeyEntity, Long>,
        JpaSpecificationExecutor<AccountWithEncryptionPublicKeyEntity> {

    /**
     * @param email AccountWithEncryptionPublicKeyEntity.email.
     * @return The AccountWithEncryptionPublicKeyEntity corresponding to the introduced email.
     */
    AccountWithEncryptionPublicKeyEntity findByEmail(
            String email);

}
