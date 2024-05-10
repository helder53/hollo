package project.hollo.Bank.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value= """
    SELECT rsv FROM Reservation rsv
    ORDER BY rsv.sendAt
    """)
    Optional<Reservation> findNearTime();
}
