package com.ewallet.transactionservice.controllers;

import com.ewallet.transactionservice.dtos.TransactionDto;
import com.ewallet.transactionservice.enums.TransactionStatus;
import com.ewallet.transactionservice.services.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction-service")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/transaction")
    public ResponseEntity<Void> initiateTransaction(@RequestBody @Valid TransactionDto transactionDto) throws URISyntaxException {
        UUID uuid=transactionService.initiatePayment(transactionDto);
        return ResponseEntity.created(new URI("/status/"+uuid)).build();
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<TransactionStatus> getTransactionStatus(@PathVariable @NotNull String id){

        return ResponseEntity.ok(transactionService.transactionStatus(id));
    }

}
