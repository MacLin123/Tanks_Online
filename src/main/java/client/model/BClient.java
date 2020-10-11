package client.model;

public class BClient {
    private static volatile Client instance;
    public static Client getInstance() {
        Client result = instance;
        if (result != null) {
            return result;
        }
        synchronized(Client.class) {
            if (instance == null) {
                instance = new Client();
            }
            return instance;
        }
    }
}
