package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String username, @RequestParam String email,
                                      @RequestParam String password, @RequestParam String firstName,
                                      @RequestParam String lastName, @RequestParam String addressLine1,
                                      @RequestParam(required = false) String addressLine2, @RequestParam String city,
                                      @RequestParam String primaryPhoneNo, @RequestParam(required = false) String secondaryPhoneNo) {

        userService.registerCustomer(username, email, password, firstName, lastName, addressLine1,
                addressLine2, city, primaryPhoneNo, secondaryPhoneNo);

        return "redirect:/login?registered";
    }
}