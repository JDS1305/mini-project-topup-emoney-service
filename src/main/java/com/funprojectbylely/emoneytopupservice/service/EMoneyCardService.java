package com.funprojectbylely.emoneytopupservice.service;

import com.funprojectbylely.emoneytopupservice.model.dto.TransactionDetailDTO;
import com.funprojectbylely.emoneytopupservice.model.entity.EMoneyCard;

import java.math.BigDecimal;

public interface EMoneyCardService {
    TransactionDetailDTO topUpBalance(String cardNumber, BigDecimal amount);

    TransactionDetailDTO checkBalance(String cardNumber);

    TransactionDetailDTO confirmAndUpdateBalance(String cardNumber);

}
