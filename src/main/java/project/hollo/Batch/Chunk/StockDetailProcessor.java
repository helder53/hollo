package project.hollo.Batch.Chunk;

import org.springframework.batch.item.ItemProcessor;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockDetail.StockDetail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockDetailProcessor implements ItemProcessor<Stock, StockDetail> {

    @Override
    public StockDetail process(Stock stock) throws Exception {
        return detailInfo(stock);
    }

    private StockDetail detailInfo(Stock stock){
        var detail = StockDetail.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .price(stock.getPrice())
                .stock_id(stock.getStock_id())
                .stock_name(stock.getStock_name())
                .build();
        return detail;
    }
}
