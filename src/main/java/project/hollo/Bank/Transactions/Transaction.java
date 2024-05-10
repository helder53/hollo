package project.hollo.Bank.Transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_id;
    private String type;
    private Long amount;
    private Long balance;
    private String createdAt;

    private String receiver;
    private String sender;
}
