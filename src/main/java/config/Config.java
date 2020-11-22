package config;

public class Config {
    public static final int MAX_PLAYERS = 4;
    // 3 - shot\sec, 10s - the greatest time to fly from edge to edge
    public static final int MAX_BULLETS = 3 * 10 * MAX_PLAYERS;
    public static final String USERS_FILE = "users.json";
    public static final int AMOUNT_LEADERS = 3;
    public enum typesClientMsg {
        CONNECT("Connect"),
        UPDATE("Update"),
        SHOT("Shot"),
        REMOVE("Remove"),
        EXIT("Exit"),
        ADDSCORE("Add_Score"),
        REGISTER("Register"),
        LOGIN("Login");
        private String type;

        typesClientMsg(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public enum typesServerMsg {
        NEWCLIENT("NewClient"),
        ID("Id"),
        REFUSE_CONNECT("RefuseConnect"),
        OK_CONNECT("OkConnect"),
        SCORE_ADDED("Score_Added"),
        REGISTER("Register"),
        LOGIN("Login"),
        LEADERS_UPDATE("Leaders_Update");
        private String type;
        typesServerMsg(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }
}
