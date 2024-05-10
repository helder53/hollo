package project.hollo.Bank.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.Bank.Request_Response.AccountRegisterRequest;
import project.hollo.Bank.Request_Response.AccountRegisterResponse;
import project.hollo.Bank.Service.AccountService;

import java.io.IOException;


@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService acntService;

    @PostMapping("/acnt_register")
    public ResponseEntity<AccountRegisterResponse> acnt_register(
            @RequestHeader("Authorization") String Header,
            @RequestBody AccountRegisterRequest request
    ){
        return ResponseEntity.ok(acntService.anct_register(Header, request));
    }

    @DeleteMapping("/acnt_delete")
    public void acnt_delete(
            @RequestHeader("Authorization") String Header,
            @RequestParam(value = "id", required = true) Long id,
            HttpServletResponse response
    ){
        acntService.acnt_delete(Header, id, response);
    }

    @GetMapping("/acnt_list_check")
    public void acnt_check(
            @RequestHeader("Authorization") String Header,
            HttpServletResponse response
    ) throws IOException {
        acntService.acnt_list_check(Header, response);
    }

    @GetMapping("/acnt_detail_check")
    public void acnt_detail_check(
            @RequestHeader("Authorization") String Header,
            @RequestParam(value = "id", required = true) Long id,
            HttpServletResponse response
    ) throws IOException {
        acntService.acnt_detail_check(Header, id, response);
    }

}
