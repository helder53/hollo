package project.hollo.Stock.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.Stock.Request_Response.StockDetailResponse;
import project.hollo.Stock.Request_Response.StockRegisterRequest;
import project.hollo.Stock.Service.StockService;

import java.io.IOException;

@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/stock_register")
    public void stock_register(
            @RequestBody StockRegisterRequest requset
    ){
        stockService.stock_register(requset);
    }

    @GetMapping("/stock_list")
    public void stock_list(
            HttpServletResponse response
    ) throws IOException {
        stockService.stock_list(response);
    }

    @GetMapping("/stock_detail")
    public ResponseEntity<StockDetailResponse> stock_detail(
            @RequestParam Long stockId
    ) throws IOException {
        return ResponseEntity.ok(stockService.stock_detail(stockId));
    }
}
