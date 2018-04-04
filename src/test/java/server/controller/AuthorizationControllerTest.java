package server.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import server.messages.Message;
import server.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
class AuthorizationControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"qwertyz\", \"email\": \"qwertyz@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void testRegisterExistEmail() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"player\", \"email\": \"player@test.ru\", \"password\": \"12345\"}"));
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"tester\", \"email\": \"player@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterExistName() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"player\", \"email\": \"player@test.ru\", \"password\": \"12345\"}"));
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"player\", \"email\": \"test@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginTest() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"test\", \"email\": \"test@test.ru\", \"password\": \"12345\"}"));
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"test\", \"email\": null, \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": null, \"email\": \"test@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void loginTestUnsuccess() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": null, \"email\": null, \"password\": null}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": null, \"email\": \"unreg@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"unreg\", \"email\": null, \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

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
    void testMeRequiresLogin() {
        final ResponseEntity<User> meResp = testRestTemplate.getForEntity("/me", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, meResp.getStatusCode());
    }
}