package project.hollo.Stock.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDetailResponse {
    private Long stockId;
    private String name;
    private List<StockPriceInfo> prices;
}
