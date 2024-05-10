package project.hollo.Bank.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import project.hollo.Bank.Request_Response.PaymentRequest;
import project.hollo.Bank.Request_Response.PaymentResponse;
import project.hollo.Bank.Request_Response.RemittanceRequest;
import project.hollo.Bank.Transactions.Transaction;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.AccountRepository;
import project.hollo.JwtToken.Jwt.JwtService;
import project.hollo.User.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    public final JwtService jwtService;
    public final AccountRepository accountRepository;
    public final TransactionRepository tsRepository;

    public void remittance(
            String header,
            RemittanceRequest request,
            HttpServletResponse response) throws IOException
    {
        User user = jwtService.HeadertoUSer(header);

        // 1. user 확인
        if (user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            log.info("유저를 찾을 수 없습니다.");
            return;
        }

        // 2. 받는 계좌 유무확인
        String receive = request.getReceiverAccountNumber();
        var receiveAccount = accountRepository.findByAccountNumber(receive);
        if (receiveAccount == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            log.info("받는 분의 계좌를 찾을 수 없습니다.");
            return;
        }

        // 3. 보내는 계좌의 잔액 확인
        var accounts = accountRepository.findByUserIdAndAccountId(user.getId(), request.getAccountId());
        Long curBalance = accounts.get().getBalance();
        if (curBalance < request.getAmount()){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            log.info("계좌에 잔액이 부족합니다.");
            return;
        }


        // 계좌 내역 저장
        var transfer = Transaction.builder()
                .type("송금")
                .amount(request.getAmount())
                .balance(curBalance - request.getAmount())
                .sender(accounts.get().getUser().getName())
                .receiver(receiveAccount.get().getUser().getName())
                .createdAt(curTime())
                .build();
        tsRepository.save(transfer);

        // 보내는 계좌의 잔액을 변경
        accounts.get().setBalance(curBalance - request.getAmount());
        accountRepository.save(accounts.get());

        // 받는 계좌의 잔액을 변경
        receiveAccount.get().setBalance(receiveAccount.get().getBalance() + request.getAmount());
        accountRepository.save(receiveAccount.get());

        // 잔액을 보여줌
        String json = new ObjectMapper().writeValueAsString(Collections.singletonMap("balance", transfer.getBalance()));
        jsonResponse(response, json);

    }

    public PaymentResponse payment(String header,
                        PaymentRequest request)
    {
        User user = jwtService.HeadertoUSer(header);

        // 1. user 확인
        if (user == null){
            log.info("유저를 찾을 수 없습니다.");
            return null;
        }

        // 2. 계좌 잔액 확인
        var acnt = accountRepository.findByUserIdAndAccountId(user.getId(), request.getAccountId());
        Long curBalance = acnt.get().getBalance();
        if (curBalance < request.getPrice()){
            log.info("계좌에 잔액이 부족합니다.");
            return null;
        }

        // 계좌 내역 저장
        var transfer = Transaction.builder()
                .type("결제")
                .amount(request.getPrice())
                .balance(curBalance - request.getPrice())
                .sender(acnt.get().getUser().getName())
                .receiver("일반 결제")
                .createdAt(curTime())
                .build();
        tsRepository.save(transfer);

        // 잔액 변경
        acnt.get().setBalance(curBalance-request.getPrice());
        accountRepository.save(acnt.get());

        return PaymentResponse.builder()
                .accountId(acnt.get().getAccount_id())
                .balance(acnt.get().getBalance())
                .build();
    }

    private String curTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZonedDateTime currentUTCDateTime = ZonedDateTime.of(currentDateTime, ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSSSSS");
        return currentUTCDateTime.format(formatter);
    }

    private void jsonResponse(HttpServletResponse response, String str) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(str);
    }
}
