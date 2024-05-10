package project.hollo.Stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    @Query(value = """
        SELECT st FROM Stock st
        WHERE st.stock_id = :id AND st.price = :price
    """)
    Optional<Stock> findByStockIdAndPrice(Long id, Long price);

    @Query("""
        SELECT st FROM Stock st
        WHERE st.stock_name = :name
    """)
    Optional<Stock> findByStockName(String name);

    long count();
}
