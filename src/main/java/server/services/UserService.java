package server.services;


import org.springframework.stereotype.Service;
import server.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@SuppressWarnings("unused")
public class UserService implements UserInterface {

    private Map<Integer, User> allUsers = new HashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    UserService() {
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("her", "her@mail.ru", "her", 10));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("warprobot", "warprobot@mail.ru", "her", 20));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("qaz", "qaz@mail.ru", "qazzaq", 27));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol1", "lol1@mail.ru", "lol1", 45));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol2", "lol2@mail.ru", "lol2", 57));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol3", "lol3@mail.ru", "lol3", 3));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol4", "lol4@mail.ru", "lol4", 58));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol5", "lol5@mail.ru", "lol5", 36));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol6", "lol6@mail.ru", "lol6", 23));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol7", "lol7@mail.ru", "lol7", 57));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol8", "lol8@mail.ru", "lol8", 234));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol9", "lol9@mail.ru", "lol9", 34));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol10", "lol10@mail.ru", "lol10", 432));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("lol11", "lol11@mail.ru", "lol11", 34));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public Integer addUser(User newUser) {
        Integer userId = ID_GENERATOR.getAndIncrement();
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
            if (login.equals(userValue.getLogin())) {
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
