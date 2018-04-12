package server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class GameController {

    public GameController() {

    }

    @Value("${FILES_DIR}")
    private String filesDir;

    @GetMapping("/level/{id}")
    public ResponseEntity<?> getMap(@PathVariable int id) {

        String path = filesDir +"maps/level_" + id + ".map";
        String body = "";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Map not found");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Map not found");
        }
        if (lines != null) {
            StringBuilder fileStr = new StringBuilder();
            for (String line : lines) {
                fileStr = fileStr.append(line + "\n");
            }
            body = fileStr.toString();
        }
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
