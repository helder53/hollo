package project.hollo.Bank.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemittanceRequest {
    private Long accountId;
    private Long amount;
    private String receiverAccountNumber;
}
