package com.funprojectbylely.emoneytopupservice.service;

import com.funprojectbylely.emoneytopupservice.model.dto.*;

import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactionsByCardNumber(String cardNumber);
    void saveTransaction(TransactionDTO transactionDTO);
}

