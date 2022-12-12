import Chat.Chat;

public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();
        try {
            chat.connect();
            chat.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
