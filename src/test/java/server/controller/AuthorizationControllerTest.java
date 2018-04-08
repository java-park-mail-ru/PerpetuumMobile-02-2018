package server.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;
import server.messages.Message;
import server.model.ChangeUser;
import server.model.User;
import server.model.UserAuth;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
class AuthorizationControllerTest {

    /**
     * Valid user email, login and password
     */
    private String login = "validLogin";
    private String email = "validEmail@test.ru";
    private String password = "validPassword@test.ru";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvc mockMvc;

    /*@Before
    void registerTestUser() throws Exception {
        // register test user
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", login, email, password)))
                .andExpect(status().isAccepted());
    }*/

    @Test
    void testRegister() throws Exception {
        // register test user
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"qwerty\", \"email\": \"qwerty@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void testRegisterExistUser() throws Exception {
        // Register user with the login that is already registered
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"email\": \"player@test.ru\", \"password\": \"12345\"}", login)))
                .andExpect(status().isForbidden());

        // Register user with the same email that is already registered
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"tester\", \"email\": \"%s\", \"password\": \"12345\"}", email)))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginTest() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", login, password)))
                .andExpect(status().isAccepted());
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void loginTestUnsuccess() throws Exception {
        // No info send
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": null, \"password\": null}"))
                .andExpect(status().isForbidden());

        // Unregistered email
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"unreg@test.ru\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());

        // Unregistered login
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"unreg\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());

        // No password specified
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": null}", email)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMeRequiresLogin() {
        final ResponseEntity<User> meResp = testRestTemplate.getForEntity("/me", User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, meResp.getStatusCode());
    }

    private List<String> loginCookie() {
        /*mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password)));*/
        final UserAuth user = new UserAuth();
        user.setPassword(password);
        user.setLogin(login);

        final HttpEntity<UserAuth> entity = new HttpEntity<>(user);
        final ResponseEntity<Message> msg = testRestTemplate.exchange("/login", HttpMethod.POST, entity, Message.class);

        return msg.getHeaders().get("Set-Cookie");
    }

    @Test
    void meAuthorized() {
        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<User> response = testRestTemplate.exchange("/me", HttpMethod.GET, requestEntity, User.class);

        final User userResp = response.getBody();

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(login, userResp.getLogin());
        assertEquals(email, userResp.getEmail());
        assertEquals("no_avatar.png", userResp.getImage());
        assertEquals(null, userResp.getPassword());
    }

    @Test
    void meUnauthorized() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        final HttpEntity<User> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/me", HttpMethod.GET, requestEntity, Message.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void logoutAuthorized() {
        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/logout", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    // Have no ideas why it is not works
    /*@Test
    void logoutUnauthorized() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<Message> response = testRestTemplate.exchange("/logout", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }*/

    @Test
    void changeLogin() {
        final String newLogin = "newLogin";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setLogin(newLogin);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setLogin(login);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void changeLoginInvalidPassword() {
        final String newLogin = "newLogin";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword("abcd");
        changeUser.setLogin(newLogin);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void changeEmail() {
        final String newEmail = "newemail@test.ru";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setEmail(newEmail);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setEmail(email);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void changeEmailInvalidPassword() {
        final String newEmail = "newemail@test.ru";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword("12345");
        changeUser.setEmail(newEmail);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        final ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void changePassword() {
        final String newPassword = "newpassword";
        ChangeUser changeUser = new ChangeUser();
        changeUser.setOldPassword(password);
        changeUser.setNewPassword(newPassword);

        final List<String> cookies = loginCookie();

        /* restoring cookie */
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, cookies);
        final HttpEntity<ChangeUser> requestEntity = new HttpEntity<>(changeUser, requestHeaders);

        ResponseEntity<Message> response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // restore test user data
        changeUser.setOldPassword(newPassword);
        changeUser.setNewPassword(password);

        response = testRestTemplate.exchange("/settings", HttpMethod.POST, requestEntity, Message.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

}