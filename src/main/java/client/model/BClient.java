package client.model;

public class BClient {
    private static volatile IClient instance;
    public static IClient getInstance() {
        IClient result = instance;
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
