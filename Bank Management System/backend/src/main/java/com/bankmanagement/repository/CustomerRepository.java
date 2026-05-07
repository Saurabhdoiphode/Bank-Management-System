package com.bankmanagement.repository;

import com.bankmanagement.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Customer Repository
 * Handles database operations for Customer entities
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    /**
     * Find customer by email
     * @param email - customer email
     * @return Optional customer
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customer by account number
     * @param accountNumber - unique account number
     * @return Optional customer
     */
    Optional<Customer> findByAccountNumber(String accountNumber);

    /**
     * Find all active customers
     * @return List of active customers
     */
    List<Customer> findByStatusAndActive(String status, Boolean active);

    /**
     * Find customers by account type
     * @param accountType - type of account
     * @return List of customers
     */
    List<Customer> findByAccountType(String accountType);

    /**
     * Find customers by name (case-insensitive)
     * @param fullName - customer name
     * @return List of customers
     */
    List<Customer> findByFullNameIgnoreCase(String fullName);

    /**
     * Find customers by partial name (contains)
     * @param fullName - partial name
     * @return List of customers
     */
    List<Customer> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Count active customers
     * @return count of active customers
     */
    long countByStatusAndActive(String status, Boolean active);
}
