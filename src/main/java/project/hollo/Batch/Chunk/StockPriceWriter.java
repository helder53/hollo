package project.hollo.Batch.Chunk;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockDetail.StockDetailRepository;
import project.hollo.Stock.StockRepository;

import java.util.List;

public class StockPriceWriter implements ItemWriter<Stock> {

    private final StockRepository stockRepository;

    public StockPriceWriter(StockRepository stock) {
        this.stockRepository = stock;
    }

    @Override
    public void write(Chunk<? extends Stock> chunk) throws Exception {
        stockRepository.saveAll(chunk.getItems());
    }
}