package project.hollo.Bank.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import project.hollo.Bank.Request_Response.AccountDetailCheck;
import project.hollo.Bank.Request_Response.AccountRegisterRequest;
import project.hollo.Bank.Request_Response.AccountRegisterResponse;
import project.hollo.Bank.Request_Response.DetailTransaction;
import project.hollo.Bank.Transactions.Transaction;
import project.hollo.Bank.Transactions.TransactionRepository;
import project.hollo.Bank.UserAccount.Account;
import project.hollo.Bank.UserAccount.AccountRepository;
import project.hollo.JwtToken.Jwt.JwtService;
import project.hollo.JwtToken.Token.TokenRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import project.hollo.User.User;

@Service
@RequiredArgsConstructor
public class AccountService {
    public final TransactionRepository transactionRepository;
    public final AccountRepository accountRepository;
    public final TokenRepository tokenRepository;
    public final JwtService jwtService;

    public AccountRegisterResponse anct_register(
            String Header,
            AccountRegisterRequest request
    ){
        User user = jwtService.HeadertoUSer(Header);

        if (user != null){
            Account acnt = Account.builder()
                    .bank_name(request.getBank_name())
                    .account_number(request.getAccount_number())
                    .balance(request.getBalance())
                    .user(user)
                    .build();

            accountRepository.save(acnt);
            return AccountRegisterResponse.builder()
                    .id(acnt.getAccount_id())
                    .bank(acnt.getBank_name())
                    .accountNumber(acnt.getAccount_number())
                    .balance(acnt.getBalance())
                    .build();
        }
        return null;
    }

    public void acnt_delete(String Header, Long id, HttpServletResponse response){
        User user = jwtService.HeadertoUSer(Header);

        if (user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        var account = accountRepository.findById(id);

        if (account.get().getUser().getId() == user.getId()){
            accountRepository.deleteById(id);
            response.setStatus(HttpStatus.OK.value());
        }
    }

    public void acnt_list_check(String Header, HttpServletResponse response) throws IOException {
        User user = jwtService.HeadertoUSer(Header);

        if (user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        var accounts = accountRepository.findTotalAccountByUserId(user.getId());
        List<AccountRegisterResponse> accountResponses = accounts.stream()
                .map(account -> AccountRegisterResponse.builder()
                        .id(account.getAccount_id())
                        .bank(account.getBank_name())
                        .accountNumber(account.getAccount_number())
                        .balance(account.getBalance())
                        .build())
                .collect(Collectors.toList());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), Collections.singletonMap("accounts", accountResponses));

    }


    public void acnt_detail_check(String Header, Long Id, HttpServletResponse response) throws IOException {
        User user = jwtService.HeadertoUSer(Header);

        if (user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        var accounts = accountRepository.findByUserIdAndAccountId(user.getId(), Id);

        if (accounts == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        List<Transaction> curTransactions = transactionRepository.findByAccountId(accounts.get().getAccount_id());

        List<DetailTransaction> detailTransactions = curTransactions.stream()
                .map(transaction -> DetailTransaction.builder()
                        .type(transaction.getType())
                        .amount(transaction.getAmount())
                        .balance(transaction.getBalance())
                        .createdAt(transaction.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());

        AccountDetailCheck detailList = AccountDetailCheck.builder()
                .bank(accounts.get().getBank_name())
                .accountNumber(accounts.get().getAccount_number())
                .balance(accounts.get().getBalance())
                .transactions(detailTransactions)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(detailList);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
