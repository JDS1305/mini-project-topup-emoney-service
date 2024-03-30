package com.funprojectbylely.emoneytopupservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NamedQuery(name = "EMoneyCard.findByCardNumber", query = "SELECT e FROM EMoneyCard e WHERE e.cardNumber = :cardNumber")
@Table(name = "emoney_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EMoneyCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "card_number")
    private String cardNumber;

    private BigDecimal balance;

    @Column(name = "temporary_balance")
    private BigDecimal temporaryBalance;

    @Column(name = "balance_confirmed")
    private boolean balanceConfirmed;

    @OneToMany(mappedBy = "eMoneyCard", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}