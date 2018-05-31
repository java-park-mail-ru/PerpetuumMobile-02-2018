package server.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import server.mechanic.game.GameSession;
import server.mechanic.game.map.GameMap;
import server.mechanic.services.GameSessionService;
import server.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.DEFAULT)
@Transactional
public class AliceServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameSessionService gameSessionService;

    private final String sessionId = "2eac4854-fce721f3-b845abba-20d60";
    private final Integer messageId = 4;
    private final String userId = "AC9WC3DF6FCE052E45A4566A48E6B7193774B84814CE49A922E163B8B29881DC";

    @Test
    void aliceWebhooksTokenNotExists() throws Exception {
        GameSessionService gms = Mockito.spy(gameSessionService);
        Mockito.doReturn(null).when(gms).getSessionForUser(any());
        MvcResult resp = mockMvc.perform(post("/alice-webhooks").contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\n" +
                                "  \"meta\": {\n" +
                                "    \"locale\": \"ru-RU\",\n" +
                                "    \"timezone\": \"Europe/Moscow\",\n" +
                                "    \"client_id\": \"ru.yandex.searchplugin/5.80 (Samsung Galaxy; Android 4.4)\"\n" +
                                "  },\n" +
                                "  \"request\": {\n" +
                                "     \"command\": \"мой токен 9983\",\n" +
                                "     \"original_utterance\": \"Алиса давай сыграем в Блендоку мой токен 9983\",\n" +
                                "     \"type\": \"SimpleUtterance\",\n" +
                                "     \"markup\": {\n" +
                                "        \"dangerous_context\": true\n" +
                                "     },\n" +
                                "     \"payload\": {}\n" +
                                "  },\n" +
                                "  \"session\": {\n" +
                                "    \"new\": true,\n" +
                                "    \"message_id\": %d,\n" +
                                "    \"session_id\": \"%s\",\n" +
                                "    \"skill_id\": \"3ad36498-f5rd-4079-a14b-788652932056\",\n" +
                                "    \"user_id\": \"%s\"\n" +
                                "  },\n" +
                                "  \"version\": \"1.0\"\n}",
                        this.messageId, this.sessionId, this.userId))).andExpect(status().isOk()).andReturn();
        final JSONObject respJson = new JSONObject(resp.getResponse().getContentAsString());
        this.checkSession(respJson);
        final JSONObject response = respJson.getJSONObject("response");
        if (!response.getString("text").equals("Кажется, такой сейчас не играет.\nПопробуете ещё раз?")) {
            throw new Exception("bad text reply");
        }
    }

    private void checkSession(JSONObject respJson) throws Exception {
        final JSONObject sessionObj = respJson.getJSONObject("session");
        if (!sessionObj.getString("session_id").equals(this.sessionId)) {
            throw new Exception("bad session_id");
        }

        if (!(userId.equals(sessionObj.getString("user_id")))) {
            System.out.println(sessionObj.getString("user_id"));
            throw new Exception("bad user_id");
        }
        if (!this.messageId.equals(sessionObj.getInt("message_id"))) {
            throw new Exception("bad message_id");
        }
    }
}