package hexlet.code.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class WelcomeController {
    @GetMapping("/welcome")
    @ResponseStatus(HttpStatus.OK)
    String greeting() {
        return "Welcome to Spring";
    }
}
