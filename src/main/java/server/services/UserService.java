package server.services;


import org.springframework.stereotype.Service;
import server.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@SuppressWarnings("unused")
public class UserService implements UserInterface {

    private Map<Integer, User> allUsers = new HashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    UserService() {
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("her", "her@mail.ru", "her"));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("warprobot", "warprobot@mail.ru", "her"));
    }

    @Override
    public Integer addUser(User newUser) {
        Integer userId = ID_GENERATOR.getAndIncrement();
        newUser.setImage("${pageContext.request.contextPath}/resources/images/no_avatar.png");
        allUsers.put(userId, newUser);
        return userId;
    }

    @Override
    public User getUserById(Integer id) {
        return allUsers.get(id);
    }

    @Override
    public Boolean isEmailRegistered(String email) {
        for (Map.Entry<Integer, User> user: allUsers.entrySet()) {
            User userValue = user.getValue();

            if (email.equals(userValue.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isLoginRegistered(String login) {
        for (Map.Entry<Integer, User> user: allUsers.entrySet()) {
            User userValue = user.getValue();
            if (login.equals(userValue.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private Integer authorizeUserByEmail(User tryAuth) {
        for (Map.Entry<Integer, User> user: allUsers.entrySet()) {
            User userValue = user.getValue();
            if (tryAuth.getPassword().equals(userValue.getPassword()) && tryAuth.getEmail().equals(userValue.getEmail())) {
                return user.getKey();
            }
        }
        return null;
    }

    private Integer authorizeUserByLogin(User tryAuth) {
        for (Map.Entry<Integer, User> user: allUsers.entrySet()) {
            User userValue = user.getValue();
            if (tryAuth.getPassword().equals(userValue.getPassword()) && tryAuth.getLogin().equals(userValue.getLogin())) {
                return user.getKey();
            }
        }
        return null;
    }

    @Override
    public Integer authorizeUser(User tryAuth) {
        if (tryAuth.getLogin() != null) {
            return authorizeUserByLogin(tryAuth);
        }
        return authorizeUserByEmail(tryAuth);
    }

    @Override
    public User checkUserById(Integer userIdInDB) {
        for (Integer userId: allUsers.keySet()) {
            if (userId.equals(userIdInDB)) {
                return allUsers.get(userId);
            }
        }
        return null;
    }
}
