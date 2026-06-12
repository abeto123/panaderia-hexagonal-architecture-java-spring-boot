package com.panaderia.ecommerce.autenticacion.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginRedirect() {
        return "login";
    }

    @GetMapping("/loginPage")
    public String login() {
        return "login";
    }
}
