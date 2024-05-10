package project.hollo.Bank.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPaymentRequest {
    private Long accountId;
    private Long price;
    private String sendAt;
}
