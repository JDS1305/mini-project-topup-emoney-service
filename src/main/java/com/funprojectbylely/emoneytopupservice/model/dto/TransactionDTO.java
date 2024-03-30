package com.funprojectbylely.emoneytopupservice.model.dto;

import com.funprojectbylely.emoneytopupservice.utils.constant.*;
import lombok.*;

import java.math.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String transactionId;
    private String cardNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Date transactionDate;
    private String sourceOfFunds;
    private TransactionStatus transactionStatus;
}
