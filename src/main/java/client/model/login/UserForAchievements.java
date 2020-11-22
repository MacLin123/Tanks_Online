package client.model.login;

public class UserForAchievements {
    String username;
    int score = 0;

    UserForAchievements(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }
}
