package com.funprojectbylely.emoneytopupservice.model.entity;

import com.funprojectbylely.emoneytopupservice.utils.constant.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private EMoneyCard eMoneyCard;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "source_of_funds")
    private String sourceOfFunds;

    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}
