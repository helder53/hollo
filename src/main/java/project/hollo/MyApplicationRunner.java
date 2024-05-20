package project.hollo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        stockDataUpload();
    }

    private void stockDataUpload() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("Data/StockData.json");

        try (InputStream inputStream = resource.getInputStream()) {
            List<Stock> stocks = objectMapper.readValue(inputStream, new TypeReference<List<Stock>>() {});

            if (stocks.size() != stockRepository.count()) {
                for (Stock stock : stocks) {
                    Optional<Stock> existStock = stockRepository.findByStockName(stock.getStock_name());

                    if (existStock.isEmpty()) {
                        stockRepository.save(stock);
                    }
                }
            }
        }
    }
}
