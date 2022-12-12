import Chat.Chat;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter nickname: ");
        String nick = sc.nextLine();

        Chat chat = new Chat(nick);
        try {
            chat.connect();
            chat.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
