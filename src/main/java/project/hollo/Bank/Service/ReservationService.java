package project.hollo.Bank.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hollo.Bank.Request_Response.ReservationPaymentRequest;
import project.hollo.Bank.Request_Response.ReservationPaymentResponse;
import project.hollo.Bank.Reservation.Reservation;
import project.hollo.Bank.Reservation.ReservationRepository;
import project.hollo.Bank.Transactions.Transaction;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.Account;
import project.hollo.Bank.UserAccount.AccountRepository;
import project.hollo.JwtToken.Jwt.JwtService;
import project.hollo.User.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    public ReservationPaymentResponse Reserve_payment(
            String header, ReservationPaymentRequest request
    ) {
        var account = AccountCheck(header, request.getAccountId());
        var sendTime = TimeChange(request.getSendAt());


        if (account.isPresent()){
            Long curBalance = account.get().getBalance();
            Long price = request.getPrice();

            if (curBalance >= price){
                var trans = Transaction.builder()
                        .type("결제 예약")
                        .amount(price)
                        .balance(curBalance - price)
                        .createdAt(curTime())
                        .receiver("")
                        .sender(account.get().getUser().getName())
                        .build();
                transactionRepository.save(trans);

                // 계좌 잔액 변경
                account.get().setBalance(curBalance-price);
                accountRepository.save(account.get());

                // 결제 예약
                var reserve = Reservation.builder()
                        .accountId(account.get().getAccount_id())
                        .price(price)
                        .sendAt(sendTime)
                        .build();
                reservationRepository.save(reserve);

                return ReservationPaymentResponse.builder()
                        .accountId(account.get().getAccount_id())
                        .balance(account.get().getBalance())
                        .sendAt(sendTime)
                        .build();
            }
        }

        return null;
    }

    private Optional<Account> AccountCheck(String Header, Long accountId){
        User user = jwtService.HeadertoUSer(Header);
        if (user != null){
            Optional<Account> account = accountRepository.findByUserIdAndAccountId(user.getId(), accountId);
            return account;
        }
        return null;
    }


    private String TimeChange(String Time){
        LocalDateTime localDateTime = LocalDateTime.parse(Time, DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSSSSS");
        return localDateTime.format(formatter);
    }

    private String curTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSSSSS");
        return currentDateTime.format(formatter);
    }

}
