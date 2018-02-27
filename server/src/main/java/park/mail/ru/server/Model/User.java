package park.mail.ru.server.Model;

@SuppressWarnings("unused")
public class User {
    private String  login;
    private String  password;
    private String  email;
    private Integer id;


    public User(String login, String password, String email, Integer id) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public User(String login, String password, String email) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
