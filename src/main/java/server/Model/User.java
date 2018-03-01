package server.Model;

@SuppressWarnings("unused")
public class User {
    private String  login;
    private String  email;
    private String  password;


    public User(String login, String email, String password) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
