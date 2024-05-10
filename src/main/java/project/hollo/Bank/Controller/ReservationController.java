package project.hollo.Bank.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.Bank.Request_Response.ReservationPaymentRequest;
import project.hollo.Bank.Request_Response.ReservationPaymentResponse;
import project.hollo.Bank.Service.ReservationService;

@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService service;

    @PostMapping("/reservation/payment")
    public ResponseEntity<ReservationPaymentResponse> rsvPayment(
            @RequestHeader("Authorization") String Header,
            @RequestBody ReservationPaymentRequest request
            ){
        return ResponseEntity.ok(service.Reserve_payment(Header, request));
    }
}
