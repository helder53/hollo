package project.hollo.StockTrading;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.hollo.Bank.UserAccount.Account;
import project.hollo.Stock.Stock;
import project.hollo.User.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stock_holdings_id;
    private String stock_name;
    private Long stock_count;
    private Long average_price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
