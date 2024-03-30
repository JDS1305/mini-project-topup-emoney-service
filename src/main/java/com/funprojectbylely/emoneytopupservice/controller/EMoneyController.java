package com.funprojectbylely.emoneytopupservice.controller;

import com.funprojectbylely.emoneytopupservice.model.dto.TransactionDTO;
import com.funprojectbylely.emoneytopupservice.model.dto.TransactionDetailDTO;
import com.funprojectbylely.emoneytopupservice.service.EMoneyCardService;
import com.funprojectbylely.emoneytopupservice.service.TransactionService;
import lombok.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.*;
import java.util.*;

@RestController
@RequestMapping("/api/emoney")
@RequiredArgsConstructor
public class EMoneyController {
    private final EMoneyCardService emoneyCardService;
    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(EMoneyController.class);

    @PostMapping("/topup")
    public ResponseEntity<TransactionDetailDTO> topUpBalance(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        BigDecimal amount = new BigDecimal(request.get("amount"));

        // Top up balance
        TransactionDetailDTO transactionDetailDTO = emoneyCardService.topUpBalance(cardNumber, amount);

        // Check if top-up is successful or failed
        if (transactionDetailDTO != null) {
            return new ResponseEntity<>(transactionDetailDTO, HttpStatus.OK);
        } else {
            logger.error("Top-up failed for card number {} with amount {}", cardNumber, amount);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Send appropriate status code for failed top-up
        }
    }

    @GetMapping("/check-balance")
    public ResponseEntity<TransactionDetailDTO> checkBalance(@RequestParam String cardNumber) {
        // Check balance
        TransactionDetailDTO transactionDetailDTO = emoneyCardService.checkBalance(cardNumber);

        // Check if balance check is successful
        if (transactionDetailDTO != null) {
            return new ResponseEntity<>(transactionDetailDTO, HttpStatus.OK);
        } else {
            logger.warn("Balance check failed for card number {}", cardNumber);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Send appropriate status code for card not found
        }
    }

    @PostMapping("/confirm-balance-update")
    public ResponseEntity<TransactionDetailDTO> confirmAndUpdateBalance(@RequestParam String cardNumber) {
        try {
            // Confirm and update balance
            TransactionDetailDTO transactionDetailDTO = emoneyCardService.confirmAndUpdateBalance(cardNumber);
            return new ResponseEntity<>(transactionDetailDTO, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("Balance update failed for card number {}: {}", cardNumber, e.getMessage());
            throw e;
        }
    }


    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByCardNumber(@RequestParam String cardNumber) {
        List<TransactionDTO> transactions = transactionService.getAllTransactionsByCardNumber(cardNumber);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/transactions")
    public ResponseEntity<String> saveTransaction(@RequestBody TransactionDTO transactionDTO) {
        transactionService.saveTransaction(transactionDTO);
        return new ResponseEntity<>("Transaction saved successfully", HttpStatus.CREATED);
    }
}