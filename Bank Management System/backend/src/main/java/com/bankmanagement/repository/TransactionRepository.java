package com.bankmanagement.repository;

import com.bankmanagement.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Repository
 * Handles database operations for Transaction entities
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * Find transactions by customer ID
     * @param customerId - customer ID
     * @return List of transactions
     */
    List<Transaction> findByCustomerId(String customerId);

    /**
     * Find transactions by account number
     * @param accountNumber - account number
     * @return List of transactions
     */
    List<Transaction> findByAccountNumber(String accountNumber);

    /**
     * Find transactions by type
     * @param type - transaction type
     * @return List of transactions
     */
    List<Transaction> findByType(String type);

    /**
     * Find transactions by status
     * @param status - transaction status
     * @return List of transactions
     */
    List<Transaction> findByStatus(String status);

    /**
     * Find transactions for a customer with limit
     * @param customerId - customer ID
     * @param limit - number of transactions
     * @return List of transactions
     */
    List<Transaction> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    /**
     * Find transactions between dates
     * @param customerId - customer ID
     * @param startDate - start date
     * @param endDate - end date
     * @return List of transactions
     */
    List<Transaction> findByCustomerIdAndCreatedAtBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all transactions ordered by date (latest first)
     * @return List of transactions
     */
    List<Transaction> findAllByOrderByCreatedAtDesc();

    /**
     * Count transactions by type
     * @param type - transaction type
     * @return count of transactions
     */
    long countByType(String type);
}
