package config;

public class Config {
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
