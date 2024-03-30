package com.funprojectbylely.emoneytopupservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.util.*;

@Entity
@Table(name = "transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "previous_balance")
    private BigDecimal previousBalance;

    @Column(name = "top_up_amount")
    private BigDecimal topUpAmount;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;
}
