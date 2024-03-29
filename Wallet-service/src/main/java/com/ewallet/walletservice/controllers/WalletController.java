package com.ewallet.walletservice.controllers;

import com.ewallet.walletservice.dtos.WalletCreditDto;
import com.ewallet.walletservice.entities.Wallet;
import com.ewallet.walletservice.enums.PaymentStatus;
import com.ewallet.walletservice.services.WalletService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/wallet-service")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Wallet> getWalletBalance(@RequestPart @NotNull String userName){
      Wallet wallet=  walletService.fetchWalletDetail(userName);
      if(wallet == null) return ResponseEntity.notFound().build();
      return ResponseEntity.ok(wallet);
    }
    @PostMapping("/add-money-to-wallet")
    public ResponseEntity<String> addMoneyToWallet(@RequestBody @Valid final WalletCreditDto walletCreditDto) throws RazorpayException, URISyntaxException {
        String url=walletService.createPaymentLink(walletCreditDto);
        return ResponseEntity.accepted().body(url);
    }


    //Callback url of Razorapy on payment completion
    //http://localhost:7072/?razorpay_payment_id=pay_NrT8tm87B2ibka&razorpay_payment_link_id=plink_NrT6cskccWea74&razorpay_payment_link_reference_id=24dfe62e-52ed-4088-aa70-eb38facdc2d3&razorpay_payment_link_status=paid&razorpay_signature=604741719840e66d608929c5e2bdc32ec7137db18f6079d5c40714a8b5edf734

    @GetMapping("/payment-status-capture")
    public ResponseEntity<PaymentStatus> verifyPaymentStatus(@RequestParam("razorpay_payment_id") String razorpay_payment_id, @RequestParam("razorpay_payment_link_id") String razorpay_payment_link_id, @RequestParam("razorpay_payment_link_reference_id") String razorpay_payment_link_reference_id, @RequestParam("razorpay_payment_link_status") String razorpay_payment_link_status, @RequestParam("razorpay_signature") String razorpay_signature) throws RazorpayException {
        PaymentStatus status = walletService.verifyPayment(razorpay_payment_id, razorpay_payment_link_id, razorpay_payment_link_reference_id, razorpay_payment_link_status, razorpay_signature);
        return ResponseEntity.ok(status);
    }
}
