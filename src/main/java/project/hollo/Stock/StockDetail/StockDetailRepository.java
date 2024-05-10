package project.hollo.Stock.StockDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockDetailRepository extends JpaRepository<StockDetail, Long> {
    @Query(value = """
    SELECT sd FROM StockDetail sd
    WHERE sd.stock_id=:id
    """)
    List<StockDetail> findByStockId(Long id);
}
