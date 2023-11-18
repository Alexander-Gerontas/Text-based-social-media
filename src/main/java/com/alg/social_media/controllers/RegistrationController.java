package com.alg.social_media.controllers;

import com.alg.social_media.exceptions.AccountExistsException;
import com.alg.social_media.objects.AccountDto;
import com.alg.social_media.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/account")
public class RegistrationController {

    private final AccountService accountService;

    @PostMapping(value = "/registration", produces = "application/json")
    public ResponseEntity<String> registration(
            @RequestBody @Valid AccountDto accountDto) throws AccountExistsException {

        log.info("Registering account with username: " + accountDto.getUsername());

        var account = accountService.accountRegistration(accountDto);
        return ResponseEntity.ok().body("account with username: "
                + account.getUsername() + " and role: " + account.getRole() + " created");
    }
}
