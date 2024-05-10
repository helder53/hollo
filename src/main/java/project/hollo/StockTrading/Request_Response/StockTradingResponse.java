package project.hollo.StockTrading.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockTradingResponse {
    private Long stockId;
    private Long amount;
    private Long averagePrice;
}
