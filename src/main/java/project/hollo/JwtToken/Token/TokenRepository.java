package project.hollo.JwtToken.Token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(value = """
            SELECT t FROM Token t
            INNER JOIN t.user u
            WHERE u.id = :id
            AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokenByUserId(Long id);

    @Query(value = """ 
        SELECT t FROM Token t
        INNER JOIN t.user u
        WHERE u.id = :id
    """)
    List<Token> findTotalTokenByUserId(Long id);

    @Query(value = """ 
        SELECT t FROM Token t
        INNER JOIN t.user u
        WHERE u.id = :id
        AND (t.expired = true and t.revoked = true)
    """)
    List<Token> findAllExpiredTokensByUserId(Long id);

    Optional<Token> findByToken(String token);
}
