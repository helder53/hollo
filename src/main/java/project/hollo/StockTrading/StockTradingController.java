package project.hollo.StockTrading;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.StockTrading.Request_Response.StockPurchaseRequest;
import project.hollo.StockTrading.Request_Response.StockSailRequest;
import project.hollo.StockTrading.Request_Response.StockTradingResponse;

@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class StockTradingController {

    private final StockTradingService tradingService;

    @PostMapping("/stock_purchase")
    public ResponseEntity<StockTradingResponse> stock_purchase(
            @RequestHeader("Authorization") String Header,
            @RequestBody StockPurchaseRequest request
    ){
        return ResponseEntity.ok(tradingService.stock_purchase(Header, request));
    }

    @PostMapping("/stock_sail")
    public ResponseEntity<StockTradingResponse> stock_sail(
            @RequestHeader("Authorization") String Header,
            @RequestBody StockSailRequest request
    ){
        return ResponseEntity.ok(tradingService.stock_sail(Header, request));
    }
}
