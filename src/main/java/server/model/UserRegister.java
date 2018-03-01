package server.model;

public class UserRegister extends User {

    private String passwordRepeat;

    UserRegister(String login, String email, String password, String passwordRepeat) {
        super(login, email, password);
        this.passwordRepeat = passwordRepeat;
    }

    UserRegister() {
        super();
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

}
