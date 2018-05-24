package server.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.DEFAULT)
@Transactional
public class GameInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    final String login = "validLogin";
    final String email = "validEmail@test.ru";
    final String password = "validPassword@test.ru";
    final Integer levelNum = 1;
    final Integer time = 1;

    @BeforeEach
    void setup() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"login\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}", login, email, password)))
                .andExpect(status().isAccepted());
    }

    void testGetMap(Integer level) throws Exception {
        mockMvc.perform(get("/level/" + level)).andExpect(status().isOk());
    }

    void testGetMapNotFound(Integer level) throws Exception {
        mockMvc.perform(get("/level/" + level)).andExpect(status().isNotFound());
    }

    @Test
    void testLevelCount() throws Exception {
        final MvcResult levelCount = mockMvc.perform(get("/levelCount")).andExpect(status().isOk()).andReturn();
        final JSONObject resp = new JSONObject(levelCount.getResponse().getContentAsString());
        final Integer maxLevel = resp.getInt("count");
        int i = 1;
        for (; i <= maxLevel; i++) {
            testGetMap(i);
        }
        testGetMapNotFound(i);
    }

    private MockHttpSession loginCookie() {
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password)))
                    .andExpect(status().isAccepted()).andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        return (MockHttpSession)result.getRequest().getSession();
    }

    @Test
    void testResults() throws Exception {
        final MockHttpSession session = loginCookie();
        mockMvc.perform(get("/results").session(session)).andExpect(status().isOk());
    }

    @Test
    void testSaveNotAuthorized() throws Exception {
        mockMvc.perform(post("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"levelNum\": \"%d\", \"time\": %d}", levelNum, time))).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void testSave() throws Exception {
        final MockHttpSession session = loginCookie();
        mockMvc.perform(post("/save").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"levelNum\": \"%d\", \"time\": %d}", levelNum, time))).andExpect(status().isAccepted());
    }

    @Test
    @Transactional
    void testSaveAndGetResults() throws Exception {
        final MockHttpSession session = loginCookie();
        mockMvc.perform(post("/save").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"levelNum\": \"%d\", \"time\": %d}", levelNum, time))).andExpect(status().isAccepted());
        MvcResult resultsRes = mockMvc.perform(get("/results").session(session)).andExpect(status().isOk()).andReturn();
        JSONArray results = new JSONArray(resultsRes.getResponse().getContentAsString());
        if (!levelNum.equals(results.getJSONObject(0).getInt("levelNum"))) {
            throw new Exception();
        }
        if (!time.equals(results.getJSONObject(0).getInt("time"))) {
            throw new Exception();
        }
    }
}
