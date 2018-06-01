package server.model;

import java.util.Objects;

@SuppressWarnings("unused")
public class User {
    private Integer id;
    private String  login;
    private String  email;
    private String  password;
    private Integer score;
    private String  image;
    private Integer token;

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public User(String login, String email, String password) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = 0;
        this.image = "no_avatar.png";
    }

    public User(Integer id, String login, String email, String password, Integer initScore) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = initScore;
        this.image = "no_avatar.png";
    }

    public User(String login, String email, String password, Integer initScore) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = initScore;
        this.image = "no_avatar.png";
    }

    public User(Integer id, String login, String email, String password, String image, Integer initScore) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.score = initScore;
        this.image = image;
    }

    public User() {
    }

    public User safeGet() {
        User userCopy = new User();
        userCopy.setImage(this.image);
        userCopy.setLogin(this.login);
        userCopy.setScore(this.score);
        return userCopy;
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

    public Integer getId() {
        return id;
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
