package config;

public class Config {
    public static final int MAX_PLAYERS = 4;
    // 3 - shot\sec, 10s - the greatest time to fly from edge to edge
    public static final int MAX_BULLETS = 3 * 10 * MAX_PLAYERS;

    public enum typesClientMsg {
        CONNECT("Connect"),
        UPDATE("Update"),
        SHOT("Shot"),
        REMOVE("Remove"),
        EXIT("Exit");
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
        ID("Id");
        private String type;
        typesServerMsg(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }
}
