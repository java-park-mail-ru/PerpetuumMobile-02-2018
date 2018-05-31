package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import server.model.SaveResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.List;

@Service
public class GameInfoService {
    @Value("${FILES_DIR}")
    private String filesDir;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameInfoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SaveResult> getUserResults(Integer userId) {
        String sql = "SELECT lv.level, score FROM public.levels lv"
                + " LEFT JOIN public.user_results ur ON ur.level_id = lv.id"
                + " WHERE ur.user_id = ?";
        List<SaveResult> result;
        try {
            result = jdbcTemplate.query(sql, (ResultSet resultSet, int ignore) -> {
                final SaveResult saveRes = new SaveResult();
                saveRes.setLevelNum(resultSet.getInt("level"));
                saveRes.setTime(resultSet.getInt("score"));
                return saveRes;
            }, userId);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer checkUserResultForLevel(Integer userId, Integer levelNum) {
        String sql = "SELECT COUNT(*) FROM public.levels LEFT JOIN public.user_results "
                + "ON public.levels.id = public.user_results.level_id "
                + "WHERE user_results.user_id = ? AND levels.level  = ?";
        Integer check = jdbcTemplate.queryForObject(sql, Integer.class, userId, levelNum);
        return check;
    }

    public Integer getLevelIdByNum(Integer levelNum) {
        String sql = "SELECT id FROM public.levels WHERE level = ?";
        Integer levelId;
        try {
            levelId = jdbcTemplate.queryForObject(sql, Integer.class, levelNum);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
        return levelId;
    }

    public Boolean tryToSaveResult(Integer userId, Integer levelId, Integer time) {
        String sql = "INSERT INTO public.user_results (level_id, user_id, score) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, levelId, userId, time);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    public Integer getUserScoreForLevel(Integer userId, Integer levelId) {
        String sql = "SELECT score FROM public.user_results WHERE user_id = ? AND level_id = ?";
        Integer score = null;
        try {
            score = jdbcTemplate.queryForObject(sql, Integer.class, userId, levelId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
        return score;
    }

    public Boolean updateResult(Integer userId, Integer levelId, Integer time) {
        String sql = "UPDATE public.user_results SET score = ? WHERE level_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sql, time, levelId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Integer getLevelsCount() {
        final String sql = "SELECT COUNT(*) from public.levels";
        Integer levelCount;
        try {
            levelCount = jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return levelCount;
    }

    public String getLevelNameByNum(Integer levelNum) {
        final String sql = "SELECT level_name from public.levels WHERE level = ?";
        String levelName;
        try {
            levelName = jdbcTemplate.queryForObject(sql, String.class, levelNum);
        } catch (EmptyResultDataAccessException e) {
            return  null;
        }
        return levelName;
    }

    public String readMapByName(String levelName) {
        String path = filesDir + "maps/" + levelName + ".map";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (lines == null) {
            return null;
        }
        StringBuilder fileStr = new StringBuilder();
        for (String line : lines) {
            fileStr = fileStr.append(line).append("\n");
        }
        return fileStr.toString();
    }
}
