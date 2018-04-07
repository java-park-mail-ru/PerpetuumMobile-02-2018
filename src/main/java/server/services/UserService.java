package server.services;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import server.model.User;
import server.dao.UserDao;
import server.mappers.UserMapper;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import server.model.UserAuth;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@SuppressWarnings("unused")
public class UserService implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    private Map<Integer, User> allUsers = new HashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    public UserService(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull List<User> getAllUsers() {
        final String sql = "SELECT * FROM public.user";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public Boolean isEmailRegistered(@NotNull String email) {
        final String sql = "SELECT count(*) from public.user WHERE email = ?";
        final Integer check = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return check != null && check > 0;
    }

    @Override
    public Boolean isLoginRegistered(@NotNull String username) {
        final String sql = "SELECT count(*) from public.user WHERE username = ?";
        final Integer check = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return check != null && check > 0;
    }

    @Override
    public @Nullable User getUserById(@NotNull Integer id) {
        final String sql = "SELECT * FROM public.user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rwNumber) -> {
                final User user = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("email"), rs.getString("password"),
                        rs.getString("image"), rs.getInt("score"));
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User getUserByUsername(@NotNull String username) {
        final String sql = "SELECT * FROM public.user WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rwNumber) -> {
                final User user = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("email"), rs.getString("password"),
                        rs.getString("image"), rs.getInt("score"));
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(@NotNull String email) {
        final String sql = "SELECT * FROM public.user WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rwNumber) -> {
                final User user = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("email"), rs.getString("password"),
                        rs.getString("image"), rs.getInt("score"));
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public @NotNull Integer addUser(@NotNull User newUser) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final Integer three = 3;
        jdbcTemplate.update(con -> {
            final PreparedStatement pst = con.prepareStatement(
                    "insert into public.user(username, email, password)" + " values(?,?,?)" + " returning id",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, newUser.getLogin());
            pst.setString(2, newUser.getEmail());
            pst.setString(three, newUser.getPassword());
            return pst;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    private Integer authorizeUserByEmail(UserAuth tryAuth) {
        User userInDB = getUserByEmail(tryAuth.getLogin());
        if (userInDB == null) {
            return null;
        }
        if (tryAuth.getPassword().equals(userInDB.getPassword())) {
            return userInDB.getId();
        }
        return null;
    }

    private Integer authorizeUserByLogin(UserAuth tryAuth) {
        User userInDB = getUserByUsername(tryAuth.getLogin());
        if (userInDB == null) {
            return null;
        }
        if (tryAuth.getPassword().equals(userInDB.getPassword())) {
            return userInDB.getId();
        }
        return null;
    }

    @Override
    public Integer authorizeUser(UserAuth tryAuth) {

        Pattern pattern = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
        Matcher matcher = pattern.matcher(tryAuth.getLogin());
        boolean isEmail = matcher.matches();

        if (isEmail) {
            return authorizeUserByEmail(tryAuth);
        }
        return authorizeUserByLogin(tryAuth);
    }

    @Override
    public User checkUserById(Integer userIdInDB) {
        return getUserById(userIdInDB);
    }
}
