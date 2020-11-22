package client.view;

public class BClientGuiF {
    private static volatile ClientGuiF instance;
    public static ClientGuiF getInstance() {
        ClientGuiF result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ClientGuiF.class) {
            if (instance == null) {
                instance = new ClientGuiF();
            }
            return instance;
        }
    }
}
