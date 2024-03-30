package com.funprojectbylely.emoneytopupservice.service.implementation;

import com.funprojectbylely.emoneytopupservice.model.dto.*;
import com.funprojectbylely.emoneytopupservice.model.entity.*;
import com.funprojectbylely.emoneytopupservice.service.*;
import com.funprojectbylely.emoneytopupservice.utils.constant.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.*;
import jakarta.transaction.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public List<TransactionDTO> getAllTransactionsByCardNumber(String cardNumber) {
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT * FROM transactions WHERE card_id = :cardId");
        nativeQuery.setParameter("cardId", cardNumber);

        List<Object[]> resultList = nativeQuery.getResultList();

        return resultList.stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionDTO transactionDTO) {
        // Periksa apakah saldo top up sudah dikonfirmasi sebelumnya
        if (transactionDTO.getTransactionType() == TransactionType.TOP_UP) {
            EMoneyCard eMoneyCard = entityManager.find(EMoneyCard.class, transactionDTO.getCardNumber());
            if (eMoneyCard != null && eMoneyCard.getTemporaryBalance().compareTo(BigDecimal.ZERO) > 0) {
                Query nativeQuery = entityManager.createNativeQuery(
                        "INSERT INTO transactions (transaction_id, card_id, transaction_type, amount, transaction_date, source_of_funds, transaction_status) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)");
                nativeQuery.setParameter(1, transactionDTO.getTransactionId());
                nativeQuery.setParameter(2, transactionDTO.getCardNumber());
                nativeQuery.setParameter(3, transactionDTO.getTransactionType().name());
                nativeQuery.setParameter(4, transactionDTO.getAmount());
                nativeQuery.setParameter(5, transactionDTO.getTransactionDate());
                nativeQuery.setParameter(6, transactionDTO.getSourceOfFunds());
                nativeQuery.setParameter(7, transactionDTO.getTransactionStatus().name());

                nativeQuery.executeUpdate();
            }
        }
    }

    private TransactionDTO mapToTransactionDTO(Object[] row) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId((String) row[0]);
        transactionDTO.setCardNumber((String) row[1]);
        // Set other properties similarly
        return transactionDTO;
    }
}