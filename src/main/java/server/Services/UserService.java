package server.Services;


import org.springframework.stereotype.Service;
import server.Model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService implements UserInterface {

    private Map<Integer, User> allUsers = new HashMap<>();
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    UserService(){
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("her", "her@mail.ru", "her"));
        allUsers.put(ID_GENERATOR.getAndIncrement(), new User("warprobot", "warprobot@mail.ru", "her"));
    }

    @Override
    public void addUser(User newUser) {

        allUsers.put(ID_GENERATOR.getAndIncrement(), newUser);
    }

    @Override
    public User getUserById(Integer id) {

        return new User();
    }

    @Override
    public Boolean isEmailRegistered(String email) {
        for(Map.Entry<Integer, User> u: allUsers.entrySet()){
            User uValue = u.getValue();
            if(email.equals(uValue.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isLoginRegistered(String login) {
        for(Map.Entry<Integer, User> u: allUsers.entrySet()){
            User uValue = u.getValue();
            if(login.equals(uValue.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer authorizeUserByEmail(User tryAuth) {
        for(Map.Entry<Integer, User> u: allUsers.entrySet()){
            User uValue = u.getValue();
            if(tryAuth.getPassword().equals(uValue.getPassword()) && tryAuth.getEmail().equals(uValue.getEmail()))
                return u.getKey();
        }
        return null;
    }

}
