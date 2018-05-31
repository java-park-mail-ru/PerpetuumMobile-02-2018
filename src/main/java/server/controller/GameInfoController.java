package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.LevelsInfo;
import server.model.SaveResult;
import server.services.GameInfoService;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class GameInfoController {


    public GameInfoController(@NotNull JdbcTemplate jdbcTemplate, GameInfoService gameInfoService) {
        this.jdbcTemplate = jdbcTemplate;
        this.gameInfoService = gameInfoService;
    }

    private final JdbcTemplate jdbcTemplate;
    private final GameInfoService gameInfoService;


    @GetMapping("/level/{num}")
    public ResponseEntity<?> getMap(@PathVariable int num) {

        String levelName = gameInfoService.getLevelNameByNum(num);
        if (levelName == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.MAP_NOT_FOUND.getMessage()));
        }

        String map = gameInfoService.readMapByName(levelName);
        if (StringUtils.isEmpty(map)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.MAP_NOT_FOUND.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }


    @GetMapping(value = "/results", produces = "application/json")
    public ResponseEntity mapResults(HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        List<SaveResult> result = gameInfoService.getUserResults(userIdInSession);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Save result for singleplayer level in DB if levelTime less than levelTime in DB
     * or if player play this level first time.
     *
     * @param saveResult (levelNum, levelTime)
     * @param httpSession cookies
     * @return message
     */
    @PostMapping(value = "/save", produces = "application/json")
    public ResponseEntity saveResult(@RequestBody SaveResult saveResult, HttpSession httpSession) {
        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        if (saveResult.getLevelNum() < 1 || saveResult.getTime() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(MessageStates.NOT_ENOUGH_DATA.getMessage()));
        }

        Integer levelId = gameInfoService.getLevelIdByNum(saveResult.getLevelNum());
        Integer userResultExist = gameInfoService.checkUserResultForLevel(userIdInSession, saveResult.getLevelNum());

        if (userResultExist.equals(0)) {
            if (!gameInfoService.tryToSaveResult(userIdInSession, levelId, saveResult.getTime())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new Message(MessageStates.SUCCESS_UPDATE.getMessage()));
        }

        Integer score = gameInfoService.getUserScoreForLevel(userIdInSession, levelId);
        if (score == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
        }

        if (score <= saveResult.getTime()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new Message(MessageStates.NOT_UPDATED.getMessage()));
        }

        Boolean status = gameInfoService.updateResult(userIdInSession, levelId, saveResult.getTime());

        if (!status) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new Message(MessageStates.NOT_UPDATED.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new Message(MessageStates.SUCCESS_UPDATE.getMessage()));
    }


    @GetMapping("/levelCount")
    public ResponseEntity<?> getLevelCount() {

        Integer levelCount = gameInfoService.getLevelsCount();
        if (levelCount == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
        }
        final LevelsInfo body = new LevelsInfo();
        body.setCount(levelCount.toString());
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
