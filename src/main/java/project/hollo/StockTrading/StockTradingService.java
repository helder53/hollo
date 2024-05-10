package project.hollo.StockTrading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.hollo.Bank.Transactions.Transaction;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.AccountRepository;
import project.hollo.JwtToken.Jwt.JwtService;
import project.hollo.Stock.StockRepository;
import project.hollo.StockTrading.Request_Response.StockPurchaseRequest;
import project.hollo.StockTrading.Request_Response.StockSailRequest;
import project.hollo.StockTrading.Request_Response.StockTradingResponse;
import project.hollo.User.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockTradingService {

    public final JwtService jwtService;
    public final AccountRepository accountRepository;
    public final StockRepository stockRepository;
    public final UserStockRepository userStockRepository;
    public final TransactionRepository tsRepository;

    public StockTradingResponse stock_purchase(String header, StockPurchaseRequest request)
    {
        User purchaser = jwtService.HeadertoUSer(header);
        // 1. user 확인
        if (purchaser == null){
            log.info("유저를 찾을 수 없습니다.");
            return null;
        }

        // 2. 계좌의 잔액 확인
        var acnt = accountRepository.findByUserIdAndAccountId(purchaser.getId(), request.getAccountId());
        if (acnt.isEmpty()){
            log.info("계좌를 찾을 수 없습니다.");
            return null;
        }

        Long curBalance = acnt.get().getBalance();
        if (curBalance < (request.getAmount()* request.getPrice())){
            log.info("계좌에 잔액이 부족합니다.");
            return null;
        }

        // 3. 주식 확인
        var stock = stockRepository.findByStockIdAndPrice(request.getStockId(), request.getPrice());
        if (stock.isEmpty()){
            log.info("주식을 찾을 수 없습니다.");
            return null;
        }

        // 3. 동일한 주식 보유 확인
        var my_stock = userStockRepository.findByStockId(purchaser.getId(), stock.get().getStock_name());

        // 4. 보유 확인 후 평균단가와 보유량 계산
        Long amount = request.getAmount();
        Long price = request.getPrice();
        Long averagePrice;
        Long totalCount;
        if (my_stock.isPresent()) {
            Long my_av_price = my_stock.get().getAverage_price();
            Long my_count = my_stock.get().getStock_count();

            Long total = (my_av_price * my_count) + (amount * price);
            averagePrice = (long) Math.floor((double) total / (my_count + amount));

            totalCount = amount + my_count;
        } else {
            averagePrice = (long) Math.floor((double) (amount * price) / amount);

            totalCount = amount;
        }


        // 5. 주식 거래 정보 저장
        var user_stock = UserStock.builder()
                .stock_name(stock.get().getStock_name())
                .stock_count(totalCount)
                .average_price(averagePrice)
                .user(purchaser)
                .build();
        userStockRepository.save(user_stock);

        // 6. 계좌 정보 갱신
        curBalance = curBalance-( amount * price );

        acnt.get().setBalance(curBalance);
        accountRepository.save(acnt.get());

        // 7. 거래 내역 저장
        var transfer = Transaction.builder()
                .type("결제")
                .amount(request.getAmount() * request.getPrice())
                .balance(curBalance)
                .sender(acnt.get().getUser().getName())
                .receiver("주식 결제_"+stock.get().getStock_name()) // 주식 결제 타입은 999
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        tsRepository.save(transfer);

        // 8. 유저의 보유 주식 현황
        return StockTradingResponse.builder()
                .stockId(request.getStockId())
                .amount(user_stock.getStock_count())
                .averagePrice(user_stock.getAverage_price())
                .build();
    }

    public StockTradingResponse stock_sail(String header, StockSailRequest request) {
        User seller = jwtService.HeadertoUSer(header);

        // 1. user 확인
        if (seller == null){
            log.info("유저를 찾을 수 없습니다.");
            return null;
        }

        // 2. 계좌 확인
        var acnt = accountRepository.findByUserIdAndAccountId(seller.getId(), request.getAccountId());
        if (acnt.isEmpty()){
            log.info("계좌를 찾을 수 없습니다.");
            return null;
        }

        // 3. 주식 정보 확인
        var stock = stockRepository.findByStockIdAndPrice(request.getStockId(), request.getPrice());
        if (stock.isEmpty()){
            log.info("주식을 찾을 수 없습니다.");
            return null;
        }

        // 4. 보유 주식 확인
        var my_stock = userStockRepository.findByUserIdAndStockId(seller.getId(), request.getStockId());
        if (my_stock.isEmpty()){
            log.info("보유 중인 주식이 없습니다.");
            return null;
        }
        if (my_stock.get().getStock_count() < request.getAmount()){
            log.info("보유하신 주보다 많은 개수를 입력했습니다.");
            return null;
        }

        // 5. 보유 주식 정보 갱신
        Long curCount = my_stock.get().getStock_count();
        Long curAvgPrice = my_stock.get().getAverage_price();

        Long totalCurValue = curCount*curAvgPrice;
        Long sellingValue = request.getAmount() * request.getPrice();
        Long myCount = curCount - request.getAmount();
        Long myAvgPrice = (long) Math.floor((double) (totalCurValue - sellingValue) / myCount);

        if (myCount == 0){
            userStockRepository.delete(my_stock.get());
        }else{
            my_stock.get().setStock_count(myCount);
            my_stock.get().setAverage_price(myAvgPrice);
            userStockRepository.save(my_stock.get());
        }

        // 6. 계좌에 수익 입금
        Long money = (request.getAmount()*request.getPrice()) + acnt.get().getBalance();
        acnt.get().setBalance(money);
        accountRepository.save(acnt.get());

        // 7. 거래 기록 저장
        var transfer = Transaction.builder()
                .type("입금")
                .amount(request.getAmount()*request.getPrice())
                .balance(money)
                .sender("(주)"+stock.get().getStock_name())
                .receiver(acnt.get().getUser().getName())
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        tsRepository.save(transfer);

        // 유저의 판매 주식 현황
        return StockTradingResponse.builder()
                .stockId(stock.get().getStock_id())
                .amount(my_stock.get().getStock_count())
                .averagePrice(my_stock.get().getAverage_price())
                .build();
    }
}
