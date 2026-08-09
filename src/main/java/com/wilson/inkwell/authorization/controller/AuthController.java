package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {

    @GetMapping("api/public-hello")
    public String printHello() {
        return "Hello from public endpoint on AuthController";
    }

}
