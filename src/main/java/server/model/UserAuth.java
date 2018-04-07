package server.model;

public class UserAuth {
    private String  email;
    private String  password;

    public UserAuth(String login, String password) {
        this.password = password;
        this.email = login;
    }

    public UserAuth() {}


    public String getLogin() {
        return email;
    }

    public void setLogin(String login) {

        this.email = login;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

}
