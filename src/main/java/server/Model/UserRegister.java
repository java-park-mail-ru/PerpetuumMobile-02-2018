package server.Model;

import java.util.Objects;

public class UserRegister extends User {

    private String password_repeat;

    UserRegister(String login, String email, String password, String password_repeat) {
        super(login, email, password);
        this.password_repeat = password_repeat;
    }

    UserRegister() {
        super();
    }

    public String getPassword_repeat() {
        return password_repeat;
    }

    public void setPassword_repeat(String password_repeat) {
        this.password_repeat = password_repeat;
    }

}
