package server.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.model.User;
import server.dao.UserDao;
import server.mappers.UserMapper;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import server.model.UserAuth;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@SuppressWarnings("unused")
public class UserService implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@NotNull JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public @NotNull List<User> getAllUsers() {
        final String sql = "SELECT * FROM public.user ORDER BY id";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public Boolean isEmailRegistered(@NotNull String email) {
        final String sql = "SELECT count(*) from public.user WHERE LOWER(email) = LOWER(?)";
        final Integer check = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return check > 0;
    }

    @Override
    public Boolean isLoginRegistered(@NotNull String username) {
        final String sql = "SELECT count(*) from public.user WHERE LOWER(username) = LOWER(?)";
        final Integer check = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return check > 0;
    }

    @Override
    public @Nullable User getUserById(@NotNull Integer id) {
        final String sql = "SELECT * FROM public.user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rwNumber) -> new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getString("image"), rs.getInt("score")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User getUserByUsername(@NotNull String username) {
        final String sql = "SELECT * FROM public.user WHERE LOWER(username) = LOWER(?)";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username},
                    (rs, rwNumber) -> new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getString("image"), rs.getInt("score")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(@NotNull String email) {
        final String sql = "SELECT * FROM public.user WHERE LOWER(email) = LOWER(?)";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email},
                    (rs, rwNumber) -> new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getString("image"), rs.getInt("score")));
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
            pst.setString(three, passwordEncoder.encode(newUser.getPassword()));
            return pst;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    private Integer authorizeUserByEmail(UserAuth tryAuth) {
        User userInDB = getUserByEmail(tryAuth.getLogin());
        if (userInDB == null) {
            return null;
        }
        if (passwordEncoder.matches(tryAuth.getPassword(), userInDB.getPassword())) {
            return userInDB.getId();
        }
        return null;
    }

    private Integer authorizeUserByLogin(UserAuth tryAuth) {
        User userInDB = getUserByUsername(tryAuth.getLogin());
        if (userInDB == null) {
            return null;
        }
        if (passwordEncoder.matches(tryAuth.getPassword(), userInDB.getPassword())) {
            return userInDB.getId();
        }
        return null;
    }

    @Override
    public Integer authorizeUser(UserAuth tryAuth) {

        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_-]+\\.)*[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*\\.[a-zA-Z]{2,6}$");
        Matcher matcher = pattern.matcher(tryAuth.getLogin());
        boolean isEmail = matcher.matches();

        if (isEmail) {
            return authorizeUserByEmail(tryAuth);
        }
        return authorizeUserByLogin(tryAuth);
    }

    @Override
    public boolean checkUserById(Integer userIdInDB) {
        return getUserById(userIdInDB) != null;
    }


    /**
     * Update user in DataBase without password.
     * @param user
     *
     * @return true if success, else false
     */
    @Override
    public boolean updateUser(User user) {
        final String sql = "UPDATE public.user SET username = ?, email = ?, image = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, user.getLogin(), user.getEmail(), user.getImage(), user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateUserPassword(User user) {
        final String sql = "UPDATE public.user SET password = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, passwordEncoder.encode(user.getPassword()), user.getId());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void increaseScoreById(@NotNull Integer userId, @NotNull Integer scoreIncrease) {
        final String sql = "UPDATE public.user SET score = score + ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql, scoreIncrease, userId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
