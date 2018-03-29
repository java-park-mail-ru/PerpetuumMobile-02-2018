package server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import server.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void getUserService() {
    }

    @Test
    void settings() {
    }

    @Test
    void logout() {
    }

    @Test
    void login() {
    }

    @Test
    void register() {
    }

    @Test
    void testMeRequiresLogin() {
        final ResponseEntity<User> meResp = testRestTemplate.getForEntity("/me", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, meResp.getStatusCode());
    }
}