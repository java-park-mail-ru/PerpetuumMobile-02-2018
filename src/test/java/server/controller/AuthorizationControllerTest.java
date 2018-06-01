package server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import server.services.JavaMailService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.DEFAULT)
@Transactional
class AuthorizationControllerTest {

    /**
     * Valid user email, login and password
     */
    private String login = "validLogin";
    private String email = "validEmail@test.ru";
    private String password = "validPassword@test.ru";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailService mailService;

    @BeforeEach
    void setup() throws Exception {
        JavaMailService jms = Mockito.spy(mailService);
        Mockito.doNothing().when(jms).sendEmail(any(), any(), any(), any());
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", login, email, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testRegisterExistingLogin() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"email\": \"email@test.ru\", \"password\": \"%s\"}", login, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterExistingEmail() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"tester\", \"email\": \"%s\", \"password\": \"%s\"}", email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testLoginEmail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testLoginNoInfo() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"null\", \"password\": \"null\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUnregEmail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"newemail@new.com\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUnregLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"newlogin\", \"password\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginNoPassword() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"password\": \"\"}", login)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"password\": \"wrong123\"}", login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMeUnauthorized() throws Exception {
        mockMvc.perform(get("/me")).andExpect(status().isUnauthorized());
    }

    private MockHttpSession loginCookie() throws Exception {
        MvcResult result;
        result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password)))
                .andExpect(status().isAccepted()).andReturn();
        return (MockHttpSession)result.getRequest().getSession();
    }

    @Test
    void meAuthorized() throws Exception {
        final MockHttpSession session = loginCookie();

        mockMvc.perform(get("/me").session(session))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("login").value(login))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("image").value("no_avatar.png"));
    }

    @Test
    void changeEmail() throws Exception {
        final String newEmail = "newemail@test.ru";
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"oldPassword\": \"%s\"}", newEmail, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void changeEmailExisting() throws Exception {
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"oldPassword\": \"%s\"}", email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeEmailInvalidPassword() throws Exception {
        final String newEmail = "newemail@test.ru";
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\": \"%s\", \"oldPassword\": \"wrong123\"}", newEmail)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeLogin() throws Exception {
        final String newLogin = "newLogin";
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"oldPassword\": \"%s\"}", newLogin, password)))
                .andExpect(status().isAccepted());
    }

    @Test
    void changeLoginExisting() throws Exception {
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"oldPassword\": \"%s\"}", login, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeLoginInvalidPassword() throws Exception {
        final String newLogin = "newLogin";
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"oldPassword\": \"wrong123\"}", newLogin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePassword() throws Exception {
        final String newPassword = "newPassword";
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/settings").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"oldPassword\": \"%s\", \"newPassword\": \"%s\"}", password, newPassword)))
                .andExpect(status().isAccepted());
    }

    @Test
    void changeSettingsUnauthorized() throws Exception {
        mockMvc.perform(post("/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"newLogin\", \"oldPassword\": \"%s\"}",password)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutAuthorized() throws Exception {
        final MockHttpSession session = loginCookie();

        mockMvc.perform(get("/me").session(session)).andExpect(status().isAccepted());
    }

    @Test
    void logoutUnauthorized() throws Exception {
        mockMvc.perform(get("/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginAuthoruzed() throws Exception {
        final MockHttpSession session = loginCookie();

        mockMvc.perform(post("/login").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password)))
                .andExpect(status().isForbidden());
    }
}