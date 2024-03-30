    package com.funprojectbylely.emoneytopupservice.model.dto;

    import lombok.*;

    import java.math.BigDecimal;
    import java.util.Date;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TransactionDetailDTO {
        private String cardNumber;
        private Date transactionDate;
        private BigDecimal previousBalance;
        private BigDecimal topUpAmount;
        private BigDecimal currentBalance;
        private String message;
    }
