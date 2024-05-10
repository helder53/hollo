package project.hollo.Stock.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import project.hollo.Stock.Request_Response.StockDetailResponse;
import project.hollo.Stock.Request_Response.StockList;
import project.hollo.Stock.Request_Response.StockPriceInfo;
import project.hollo.Stock.Request_Response.StockRegisterRequest;
import project.hollo.Stock.Stock;
import project.hollo.Stock.StockDetail.StockDetail;
import project.hollo.Stock.StockDetail.StockDetailRepository;
import project.hollo.Stock.StockRepository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RestController
public class StockService {
    private final StockRepository stockRepository;
    private final StockDetailRepository detailRepository;

    public void stock_register(StockRegisterRequest reqeust){
        var stock = Stock.builder()
                .stock_name(reqeust.getStock_name())
                .price(reqeust.getPrice())
                .build();

        stockRepository.save(stock);
    }

    public void stock_list(HttpServletResponse response) throws IOException {
        List<Stock> stocks = stockRepository.findAll();
        List<StockList> stockLists = stocks.stream()
                .map(this::convertToStockList)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Collections.singletonMap("stocks", stockLists));

        jsonResponse(response, json);
    }

    public StockDetailResponse stock_detail(Long stockId)
        throws IOException{
            var stock = stockRepository.findById(stockId);
            var stockdetail = detailRepository.findByStockId(stockId);

            List<StockPriceInfo> priceInfos = stockdetail.stream()
                    .map( info -> StockPriceInfo.builder()
                            .price(info.getPrice())
                            .createdAt(info.getCreatedAt())
                            .build()
                    ).collect(Collectors.toList());

            return StockDetailResponse.builder()
                    .stockId(stock.get().getStock_id())
                    .name(stock.get().getStock_name())
                    .prices(priceInfos)
                    .build();
        }


    private StockList convertToStockList(Stock stock) {
        return StockList.builder()
                .id(stock.getStock_id())
                .name(stock.getStock_name())
                .price(stock.getPrice())
                .build();
    }

    private void jsonResponse(HttpServletResponse response, String str) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(str);
    }
}
