package project.hollo.Bank.UserAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(value = """
            SELECT ac FROM Account ac
            INNER JOIN ac.user u
            WHERE u.id = :id
        """)
    List<Account> findTotalAccountByUserId(Long id);

    @Query(value = """
        SELECT ac FROM Account ac
        INNER JOIN ac.user u
        WHERE u.id = :userid AND ac.account_id = :accountId
    """)
    Optional<Account> findByUserIdAndAccountId(Long userid, Long accountId);

    @Query(value = """
        SELECT ac FROM Account ac
        WHERE ac.account_number = :accountNumber
    """)
    Optional<Account> findByAccountNumber(String accountNumber);
}
