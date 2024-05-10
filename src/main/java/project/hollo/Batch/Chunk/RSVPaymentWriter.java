package project.hollo.Batch.Chunk;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import project.hollo.Bank.Reservation.Reservation;
import project.hollo.Bank.Reservation.ReservationRepository;

public class RSVPaymentWriter implements ItemWriter<Reservation> {
    private final ReservationRepository reservationRepository;

    public RSVPaymentWriter(ReservationRepository repository){
        this.reservationRepository = repository;
    }


    @Override
    public void write(Chunk<? extends Reservation> chunk) throws Exception {
        reservationRepository.saveAll(chunk.getItems());
    }
}
