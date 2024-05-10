package project.hollo.Batch.Chunk;

import org.springframework.batch.item.ItemProcessor;
import project.hollo.Bank.Reservation.Reservation;
import project.hollo.Bank.Reservation.ReservationRepository;
import project.hollo.Bank.Transactions.Transaction;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.Account;
import project.hollo.Bank.UserAccount.AccountRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class RSVPaymentProcessor implements ItemProcessor<Reservation, Reservation> {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private ReservationRepository reservationRepository;

    public RSVPaymentProcessor(
            TransactionRepository trsRepository,
            AccountRepository acntRepository,
            ReservationRepository rsvRepository
            ){
        this.transactionRepository = trsRepository;
        this.accountRepository = acntRepository;
        this.reservationRepository = rsvRepository;
    }

    @Override
    public Reservation process(Reservation item) throws Exception, NullPointerException {
        var sendTime = item.getSendAt();
        if (timeCheck(sendTime)){
            var Account = accountRepository.findById(item.getAccountId());
            payment(item, Account);
        }

        return null;
    }

    private void payment(Reservation item, Optional<Account> account){
        if (account.isPresent()){
            // 거래 내역 등록
            var trans = Transaction.builder()
                    .type("예약 결제 승인")
                    .amount(item.getPrice())
                    .balance(account.get().getBalance())
                    .receiver("")
                    .sender(account.get().getUser().getName())
                    .createdAt(item.getSendAt())
                    .build();
            transactionRepository.save(trans);

            // 예약 삭제
            reservationRepository.delete(item);
        }
    }

    private boolean timeCheck(String sendAt) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSSSSS");
        LocalDateTime sendTime = LocalDateTime.parse(sendAt, formatter);
        boolean chk = !sendTime.isAfter(currentDateTime);

        return chk;
    }

}
