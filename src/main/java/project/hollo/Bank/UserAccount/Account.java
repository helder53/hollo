package project.hollo.Bank.UserAccount;

import jakarta.persistence.*;
import lombok.*;
import project.hollo.User.User;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long account_id;
    private String bank_name;

    @Column(unique = true)
    private String account_number;
    private Long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

}
