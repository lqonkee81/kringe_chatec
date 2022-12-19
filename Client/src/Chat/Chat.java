package Chat;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;


import Package.*;

public class Chat {
    private int PORT;
    private String IP_ADD;
    private final Scanner sc = new Scanner(System.in);

    private Socket socket = null;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    private Message inMessage;
    private Message outMessage;

    private RSA rsa;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private PublicKey publicKeyServer;

    private String nickname;


    public Chat(String nickname, String ip_add, int PORT) {
        this.PORT = PORT;
        this.IP_ADD = ip_add;
        this.nickname = nickname;

        rsa = new RSA();
        privateKey = rsa.getPrivateKey();
        publicKey = rsa.getPublicKey();
    }

    public void connect() throws IOException {
        this.socket = new Socket(IP_ADD, PORT);

        this.writer = new ObjectOutputStream(socket.getOutputStream()); // Поток для отправки пакета
        this.reader = new ObjectInputStream(socket.getInputStream());   // Поток для чтения пакета

        System.out.println("Connected to server");
    }

    public void run() {
        exchangeKeys();
        preparing();
    }

    private void preparing() {
        /*
         * Отпровка серверу никнейма пользователя
         */

        try {
            outMessage = new Message("", nickname);
            sendMessage(outMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exchangeKeys() {
        /*
         * Обмен публичными ключами с сервером
         *
         * Сначала клиент получает публичный ключ сервера
         * Затем отправляет свой
         */
        try {
//            inMessage = (Message) reader.readObject();
            this.publicKeyServer = (PublicKey) reader.readObject();
            System.out.println("Getting public key from server");
            System.out.println(publicKeyServer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            outMessage = new Message(this.publicKey);

            writer.writeObject(publicKey);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        /*
         * Отправка пакета серверу
         */
        try {
            msg = rsa.encrypt(msg, publicKeyServer);
            writer.writeObject(msg);
            writer.flush();
        } catch (SocketException e) {
            System.out.println("Ты не подлючен, дэбил!");
        } catch (IOException e) {
            System.out.println("Cannot send package");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Failed to encrypt message");
        }
    }

    public Message getMessage() throws Exception {
        inMessage = (Message) reader.readObject();
        if (!inMessage.getValue().equals("")) {
            inMessage = rsa.decrypt(inMessage, privateKey);
            return inMessage;
        }
        return null;
    }

    public void disconnect() {
        try {
            this.reader.close();
            this.writer.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}