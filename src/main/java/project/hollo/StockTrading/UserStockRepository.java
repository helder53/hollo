package project.hollo.StockTrading;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    @Query(value = """
        SELECT u_stock FROM UserStock u_stock
        INNER JOIN u_stock.user u
        WHERE u.id = :id
        AND u_stock.stock_name = :name
    """)
    Optional<UserStock> findByStockId(Long id, String name);

    @Query(value = """
        SELECT u_stock FROM UserStock u_stock
        INNER JOIN u_stock.user u
        WHERE u.id = :id
        AND u_stock.stock_name = (
            SELECT s.stock_name FROM Stock s WHERE s.stock_id = :stock_id
        )
    """)
    Optional<UserStock> findByUserIdAndStockId(Long id, Long stock_id);
}
