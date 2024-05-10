package project.hollo.Bank.Transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.hollo.Bank.UserAccount.Account;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT ts FROM Transaction ts
        WHERE ts.sender = (
            SELECT u.name FROM User u WHERE u.id = :id
        )
    """)
    List<Transaction> findByAccountId(Long id);
}
