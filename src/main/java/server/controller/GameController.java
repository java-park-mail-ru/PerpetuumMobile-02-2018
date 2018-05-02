package server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.LevelsInfo;
import server.model.SaveResult;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class GameController {

    public GameController(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${FILES_DIR}")
    private String filesDir;

    private final JdbcTemplate jdbcTemplate;


    @GetMapping("/level/{id}")
    public ResponseEntity<?> getMap(@PathVariable int id) {
        final String sql = "SELECT level_name from public.levels WHERE level = ?";
        String levelName;
        try {
            levelName = jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.MAP_NOT_FOUND.getMessage()));
        }


        String path = filesDir + "maps/" + levelName + ".map";
        String body = "";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.MAP_NOT_FOUND.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(MessageStates.MAP_NOT_FOUND.getMessage()));
        }
        if (lines != null) {
            StringBuilder fileStr = new StringBuilder();
            for (String line : lines) {
                fileStr = fileStr.append(line).append("\n");
            }
            body = fileStr.toString();
        }
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping(value = "/save", produces = "application/json")
    public ResponseEntity saveResult(@RequestBody SaveResult saveResult, HttpSession httpSession) {

        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        if (userIdInSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(MessageStates.UNAUTHORIZED.getMessage()));
        }

        if (saveResult.getLevelNum() < 1 || saveResult.getTime() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(MessageStates.NOT_ENOUGH_DATA.getMessage()));
        }

        String sql = "SELECT COUNT(*) FROM public.levels LEFT JOIN public.user_results "
                + "ON public.levels.id = public.user_results.level_id "
                + "WHERE user_results.user_id = ? AND levels.level  = ?";
        Integer check = jdbcTemplate.queryForObject(sql, Integer.class, userIdInSession, saveResult.getLevelNum());

        Integer levelId;
        sql = "SELECT id FROM public.levels WHERE level = ?";
        try {
            levelId = jdbcTemplate.queryForObject(sql, Integer.class, saveResult.getLevelNum());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message(MessageStates.LEVEL_NOT_FOUND.getMessage()));
        }

        if (check == 0) {
            sql = "INSERT INTO public.user_results (level_id, user_id, score) VALUES (?, ?, ?)";
            try {
                jdbcTemplate.update(sql, levelId, userIdInSession, saveResult.getTime());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new Message(MessageStates.SUCCESS_UPDATE.getMessage()));
        }

        Integer score;
        sql = "SELECT score FROM public.user_results WHERE user_id = ? AND level_id = ?";
        try {
            score = jdbcTemplate.queryForObject(sql, Integer.class, userIdInSession, levelId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
        }

        if (score <= saveResult.getTime()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .body(new Message(MessageStates.NOT_UPDATED.getMessage()));
        }

        sql = "UPDATE public.user_results SET score = ? WHERE level_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sql, saveResult.getTime(), levelId, userIdInSession);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new Message(MessageStates.SUCCESS_UPDATE.getMessage()));
    }


    @GetMapping("/levelCount")
    public ResponseEntity<?> getMap() {
        final String sql = "SELECT COUNT(*) from public.levels";
        String levelCount;
        try {
            levelCount = jdbcTemplate.queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message(MessageStates.DATABASE_ERROR.getMessage()));
        }
        final LevelsInfo body = new LevelsInfo();
        body.setCount(levelCount);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
