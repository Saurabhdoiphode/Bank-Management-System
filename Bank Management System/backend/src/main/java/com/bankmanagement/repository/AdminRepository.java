package com.bankmanagement.repository;

import com.bankmanagement.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Admin Repository
 * Handles database operations for Admin entities
 */
@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    /**
     * Find admin by email
     * @param email - admin email
     * @return Optional admin
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Find admin by email and active status
     * @param email - admin email
     * @param active - active status
     * @return Optional admin
     */
    Optional<Admin> findByEmailAndActive(String email, Boolean active);
}
