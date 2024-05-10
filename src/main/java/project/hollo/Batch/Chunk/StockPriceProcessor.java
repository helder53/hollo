package project.hollo.Batch.Chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import project.hollo.Stock.Stock;

import java.util.Random;

public class StockPriceProcessor implements ItemProcessor<Stock, Stock> {

    private static final double MAX_PERCENT = 0.3;
    private static final double MIN_PERCENT = -0.3;
    private static final double MIN_PRICE = 1000;

    @Override
    public Stock process(Stock stock) throws Exception {
        Long nextPrice = priceFluctuation(stock.getPrice());
        stock.setPrice(nextPrice);
        return stock;
    }

    private Long priceFluctuation(Long price){
        // price 가격이 원래 가격의 +30%와 -30%에서의 랜덤한 값(단위 : 5%) 변동이 있게 한다.
        var RandomNumber = new Random().nextInt(13); // 0부터 12까지의 난수 생성
        double percent = MIN_PERCENT + RandomNumber * 0.05; // -30% ~ +30% 범위의 난수 생성
        double newPrice = Math.round(price * (1 + percent) / 100.0) * 100; // 100원 단위

        // 모든 주식의 가격은 1000미만으로 내려가지 않는다.
        if (newPrice < MIN_PRICE){
            newPrice = MIN_PRICE;
        }

        return (long)newPrice;
    }
}
