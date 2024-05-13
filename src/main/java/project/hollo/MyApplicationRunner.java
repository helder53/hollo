package project.hollo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        stockDataUpload();
    }

    private void stockDataUpload() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:/Data/StockData.json");
        List<Stock> stocks = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Stock>>(){});

        if (stocks.size() != stockRepository.count()){
            for (Stock stock : stocks){
                Optional<Stock> existStock = stockRepository.findByStockName(stock.getStock_name());

                if (existStock.isEmpty()){
                    stockRepository.save(stock);
                }
            }
        };
    }

}
