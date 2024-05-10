package project.hollo.Batch.Chunk;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import project.hollo.Stock.StockDetail.StockDetail;
import project.hollo.Stock.StockDetail.StockDetailRepository;

public class StockDetailWriter implements ItemWriter<StockDetail> {

    private final StockDetailRepository repository;

    public StockDetailWriter(StockDetailRepository repository){
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends StockDetail> chunk) throws Exception {
        repository.saveAll(chunk.getItems());
    }
}
