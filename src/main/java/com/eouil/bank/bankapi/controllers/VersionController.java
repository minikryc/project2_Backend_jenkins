package com.eouil.bank.bankapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @GetMapping("/version")
    public String getVersion() {
        return "v1.0.1 - Jenkins CI/CD 배포됨";
    }
}
