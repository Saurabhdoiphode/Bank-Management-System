package com.bankmanagement.service;

import com.bankmanagement.dto.DepositRequest;
import com.bankmanagement.dto.TransferRequest;
import com.bankmanagement.dto.WithdrawRequest;
import com.bankmanagement.model.Customer;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.repository.CustomerRepository;
import com.bankmanagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for transaction-related operations
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    /**
     * Deposit money to account
     * @param customerId - customer ID
     * @param request - deposit request
     * @return Transaction object
     */
    public Transaction deposit(String customerId, DepositRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);

        if (customerOpt.isEmpty()) {
            return null;
        }

        Customer customer = customerOpt.get();
        Double balanceBefore = customer.getBalance();

        // Update customer balance
        customer.setBalance(balanceBefore + request.getAmount());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        // Create transaction record
        Transaction transaction = new Transaction(
                customerId,
                customer.getAccountNumber(),
                customer.getFullName(),
                "DEPOSIT",
                request.getAmount(),
                balanceBefore,
                customer.getBalance(),
                request.getDescription() != null ? request.getDescription() : "Deposit",
                request.getMethod()
        );

        return transactionRepository.save(transaction);
    }

    /**
     * Withdraw money from account
     * @param customerId - customer ID
     * @param request - withdraw request
     * @return Transaction object or null if insufficient balance
     */
    public Transaction withdraw(String customerId, WithdrawRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);

        if (customerOpt.isEmpty()) {
            return null;
        }

        Customer customer = customerOpt.get();
        Double balanceBefore = customer.getBalance();

        // Check sufficient balance
        if (balanceBefore < request.getAmount()) {
            return null; // Insufficient balance
        }

        // Update customer balance
        customer.setBalance(balanceBefore - request.getAmount());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        // Create transaction record
        Transaction transaction = new Transaction(
                customerId,
                customer.getAccountNumber(),
                customer.getFullName(),
                "WITHDRAW",
                request.getAmount(),
                balanceBefore,
                customer.getBalance(),
                request.getDescription() != null ? request.getDescription() : "Withdrawal",
                request.getWithdrawType()
        );

        return transactionRepository.save(transaction);
    }

    /**
     * Transfer money to another account
     * @param customerId - sender customer ID
     * @param request - transfer request
     * @return Transaction object or null if transfer failed
     */
    public Transaction transfer(String customerId, TransferRequest request) {
        // Get sender customer
        Optional<Customer> senderOpt = customerRepository.findById(customerId);
        if (senderOpt.isEmpty()) {
            return null;
        }

        Customer sender = senderOpt.get();

        // Get recipient customer
        Optional<Customer> recipientOpt = customerRepository.findByAccountNumber(request.getRecipientAccountNumber());
        if (recipientOpt.isEmpty()) {
            return null;
        }

        Customer recipient = recipientOpt.get();

        // Check sufficient balance
        if (sender.getBalance() < request.getAmount()) {
            return null; // Insufficient balance
        }

        Double senderBalanceBefore = sender.getBalance();
        Double recipientBalanceBefore = recipient.getBalance();

        // Deduct from sender
        sender.setBalance(senderBalanceBefore - request.getAmount());
        sender.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(sender);

        // Add to recipient
        recipient.setBalance(recipientBalanceBefore + request.getAmount());
        recipient.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(recipient);

        // Create transaction record for sender (outgoing transfer)
        Transaction senderTransaction = new Transaction(
                customerId,
                sender.getAccountNumber(),
                sender.getFullName(),
                "TRANSFER",
                request.getAmount(),
                senderBalanceBefore,
                sender.getBalance(),
                request.getDescription() != null ? request.getDescription() : "Transfer to " + recipient.getFullName(),
                null
        );
        senderTransaction.setRecipientAccountNumber(recipient.getAccountNumber());
        senderTransaction.setRecipientName(recipient.getFullName());

        Transaction savedTransaction = transactionRepository.save(senderTransaction);

        // Create transaction record for recipient (incoming transfer)
        Transaction recipientTransaction = new Transaction(
                recipient.getId(),
                recipient.getAccountNumber(),
                recipient.getFullName(),
                "TRANSFER_RECEIVED",
                request.getAmount(),
                recipientBalanceBefore,
                recipient.getBalance(),
                "Transfer from " + sender.getFullName(),
                null
        );
        recipientTransaction.setReferenceId(savedTransaction.getReferenceId());

        transactionRepository.save(recipientTransaction);

        return savedTransaction;
    }

    /**
     * Get customer transactions
     * @param customerId - customer ID
     * @return List of transactions
     */
    public List<Transaction> getCustomerTransactions(String customerId) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    /**
     * Get recent transactions for customer
     * @param customerId - customer ID
     * @param limit - number of recent transactions
     * @return List of recent transactions
     */
    public List<Transaction> getRecentTransactions(String customerId, int limit) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get all transactions (admin)
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get transactions by type
     * @param type - transaction type
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByType(String type) {
        return transactionRepository.findByType(type);
    }

    /**
     * Get transaction statistics
     * @return Transaction statistics
     */
    public Long getTotalTransactionsCount() {
        return transactionRepository.count();
    }

    /**
     * Get total deposit amount
     * @return Total deposit amount
     */
    public Double getTotalDepositAmount() {
        return transactionRepository.findByType("DEPOSIT")
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Get total withdrawal amount
     * @return Total withdrawal amount
     */
    public Double getTotalWithdrawalAmount() {
        return transactionRepository.findByType("WITHDRAW")
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
