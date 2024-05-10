package project.hollo.Bank.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.Bank.Request_Response.PaymentRequest;
import project.hollo.Bank.Request_Response.PaymentResponse;
import project.hollo.Bank.Request_Response.RemittanceRequest;
import project.hollo.Bank.Service.TransactionService;

import java.io.IOException;

@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/remittance")
    public void remittance(
            @RequestHeader("Authorization") String Header,
            @RequestBody RemittanceRequest request,
            HttpServletResponse response
    ) throws IOException {
            transactionService.remittance(Header, request, response);
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> buy(
        @RequestHeader("Authorization") String Header,
        @RequestBody PaymentRequest request
    ){
        return ResponseEntity.ok(transactionService.payment(Header, request));
    }


}
