package project.hollo.Stock.StockDetail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class StockDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stock_detail_id;
    private Long stock_id;
    private String stock_name;
    private Long price;
    private String createdAt;
}
