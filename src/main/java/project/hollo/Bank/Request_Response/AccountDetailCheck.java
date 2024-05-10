package project.hollo.Bank.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailCheck {
    private String bank;
    private String accountNumber;
    private Long balance;
    private List<DetailTransaction> transactions;
}
