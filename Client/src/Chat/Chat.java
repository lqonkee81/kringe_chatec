package Chat;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;


import Package.*;

public class Chat {
    private final int PORT = 8721;
    private final String IP_ADD = "127.0.0.1";
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


    public Chat(String nickname) {
        this.nickname = nickname;
        rsa = new RSA();
        privateKey = rsa.getPrivateKey();
        publicKey = rsa.getPublicKey();
    }

    public void connect() throws IOException {
        this.socket = new Socket(IP_ADD, PORT);

        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected to server");
    }

    public void run() {
        exchangeKeys();
        preparing();
    }

    private void preparing() {
        try {
            outMessage = new Message("", nickname);
            sendMessage(outMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exchangeKeys() {
        try {
            inMessage = (Message) reader.readObject();
            this.publicKeyServer = (PublicKey) inMessage.getObj();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            outMessage = new Message(this.publicKey);

            writer.writeObject(outMessage);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        try {
            msg.setValue(rsa.encrypt(msg.getValue(), publicKeyServer));
            writer.writeObject(msg);
            writer.flush();
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
            System.out.println("get message");
            inMessage.setValue(rsa.decrypt(inMessage.getValue(), privateKey));
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