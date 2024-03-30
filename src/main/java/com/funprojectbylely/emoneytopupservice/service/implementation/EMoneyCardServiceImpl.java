package com.funprojectbylely.emoneytopupservice.service.implementation;


import com.funprojectbylely.emoneytopupservice.model.dto.TransactionDTO;
import com.funprojectbylely.emoneytopupservice.model.dto.TransactionDetailDTO;
import com.funprojectbylely.emoneytopupservice.model.entity.*;
import com.funprojectbylely.emoneytopupservice.service.*;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EMoneyCardServiceImpl implements EMoneyCardService {

    @PersistenceContext
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(EMoneyCardServiceImpl.class);

    @Override
    @Transactional
    public TransactionDetailDTO topUpBalance(String cardNumber, BigDecimal amount) {
        EMoneyCard eMoneyCard = findByCardNumber(cardNumber);
        BigDecimal newBalance;

        BigDecimal previousBalance = null;
        if (eMoneyCard != null && eMoneyCard.getBalance() != null) {
            previousBalance = eMoneyCard.getBalance();
        }

        System.out.println(cardNumber);
        System.out.println(eMoneyCard);

        if (eMoneyCard == null) {
            // Create new EMoneyCard if not exists
            eMoneyCard = new EMoneyCard();
            eMoneyCard.setCardNumber(cardNumber);
            eMoneyCard.setTemporaryBalance(amount);
            entityManager.persist(eMoneyCard);
        } else {
            // Top up balance if EMoneyCard exists
            BigDecimal currentBalance = eMoneyCard.getBalance();
            BigDecimal temporaryBalance = eMoneyCard.getTemporaryBalance();
            eMoneyCard.setTemporaryBalance(temporaryBalance.add(amount));
            eMoneyCard.setBalanceConfirmed(false);
            entityManager.merge(eMoneyCard);

        }
        TransactionDetailDTO transactionDetailDTO = saveTransactionDetail(cardNumber, amount, previousBalance, previousBalance, "Top-up success!");

        // Log the top-up success message
        if (transactionDetailDTO != null) {
            logger.info("Top-up success for card number {}: amount: {}, previous balance: {}", cardNumber, amount, previousBalance);
        } else {
            logger.error("Top-up failed for card number {}: amount: {}", cardNumber, amount);
        }

        return transactionDetailDTO;
    }

    private TransactionDetailDTO saveTransactionDetail(String cardNumber, BigDecimal amount, BigDecimal currentBalance, BigDecimal previousBalance, String message) {
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .cardNumber(cardNumber)
                .transactionDate(new Date())
                .topUpAmount(amount)
                .previousBalance(previousBalance)
                .currentBalance(currentBalance)
                .build();
        entityManager.persist(transactionDetail);

        // Check if transactionDetail is saved successfully
        if (transactionDetail.getId() != null) {
            logger.info("Transaction detail saved successfully for card number {}: amount: {}, previous balance: {}, current balance: {}", cardNumber, amount, previousBalance, currentBalance);
            return createTransactionDetailDTO(cardNumber, previousBalance, amount, currentBalance, message);
        } else {
            logger.error("Failed to save transaction detail for card number {}: amount: {}, previous balance: {}, current balance: {}", cardNumber, amount, previousBalance, currentBalance);
            return createTransactionDetailDTO(cardNumber, previousBalance, amount, currentBalance, "Failed to save transaction detail");
        }
    }

    private TransactionDetailDTO createTransactionDetailDTO(String cardNumber, BigDecimal previousBalance, BigDecimal amount, BigDecimal newBalance, String message) {
//        logger.info("Creating transaction detail DTO for card number {}: amount: {}, previous balance: {}, new balance: {}", cardNumber, amount, previousBalance, newBalance);
        return new TransactionDetailDTO(cardNumber, new Date(), previousBalance, amount, newBalance, message);
    }

    @Override
    public TransactionDetailDTO checkBalance(String cardNumber) {
        EMoneyCard eMoneyCard = findByCardNumber(cardNumber);
        if (eMoneyCard != null) {
            BigDecimal balance = eMoneyCard.getBalance();
            // Log successful balance check message
            logger.info("Balance check successful for card number {}: balance: {}", cardNumber, balance);
            return createTransactionDetailDTO(cardNumber, balance, BigDecimal.ZERO, balance, "Balance check successful");
        } else {
            // Log card not found message and throw NotFoundException
            logger.warn("Card not found for card number: {}", cardNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found for card number: " + cardNumber);
        }
    }

    @Override
    @Transactional
    public TransactionDetailDTO confirmAndUpdateBalance(String cardNumber) {
        EMoneyCard eMoneyCard = findByCardNumber(cardNumber);
        if (eMoneyCard != null) {
            BigDecimal temporaryBalance = eMoneyCard.getTemporaryBalance();
            if (temporaryBalance != null) {
                BigDecimal currentBalance = eMoneyCard.getBalance();
//                if (currentBalance != null) {
//                    BigDecimal newBalance = currentBalance.add(temporaryBalance);
                    BigDecimal newBalance;
                    if (currentBalance != null) {
                        newBalance = currentBalance.add(temporaryBalance);
                    } else {
                        newBalance = temporaryBalance; // If currentBalance is null, newBalance will be the temporary balance
                    }
                    eMoneyCard.setBalance(newBalance);
                    eMoneyCard.setTemporaryBalance(BigDecimal.ZERO);
                    eMoneyCard.setBalanceConfirmed(true);
                    entityManager.merge(eMoneyCard);

                    // Save transaction detail
                    TransactionDetailDTO transactionDetailDTO = saveTransactionDetail(cardNumber, temporaryBalance, newBalance, currentBalance, "Balance update confirmed");
                    // Log successful balance update message
                    System.out.println("Balance update confirmed for card number {}: new balance: " + cardNumber + " " + newBalance);
                    return transactionDetailDTO;
//                } else {
//                    // Log error for null currentBalance
//                    logger.error("Failed to update balance for card number {}: currentBalance is null", cardNumber);
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update balance: currentBalance is null");
//                }
            } else {
                // Log warning for null temporaryBalance
                logger.warn("Temporary balance is null for card number: {}", cardNumber);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update balance: temporary balance is null");
            }
        } else {
            // Log warning for card not found
            logger.warn("Card not found for card number: {}", cardNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found for card number: " + cardNumber);
        }
    }

    private EMoneyCard findByCardNumber(String cardNumber) {
        try {
            return entityManager.createQuery("SELECT e FROM EMoneyCard e WHERE e.cardNumber = :cardNumber", EMoneyCard.class)
                    .setParameter("cardNumber", cardNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            // Handle the case where no result is found
            return null; // or throw a custom exception, log, etc.
        }
    }


    private TransactionDetailDTO saveTransactionDetail(String cardNumber, BigDecimal amount, BigDecimal currentBalance) {
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .cardNumber(cardNumber)
                .transactionDate(new Date())
                .topUpAmount(amount)
                .previousBalance(currentBalance.subtract(amount))
                .currentBalance(currentBalance)
                .build();
        entityManager.persist(transactionDetail);
        return createTransactionDetailDTO(cardNumber, new BigDecimal("0"), amount, currentBalance, "Transaction saved successfully");
    }
}