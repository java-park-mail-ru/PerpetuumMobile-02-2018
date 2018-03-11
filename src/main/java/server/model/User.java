package server.model;

@SuppressWarnings("unused")
public class User {
    private String  login;
    private String  email;
    private String  password;
    private Integer score;
    private String  image;

    public User(String login, String email, String password) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = 0;
    }

    public User(String login, String email, String password, Integer initScore) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = initScore;
    }

    public User() {
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer newScore) {
        score = newScore;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
