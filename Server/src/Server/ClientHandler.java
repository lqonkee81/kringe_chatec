package Server;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HexFormat;

import Package.*;

public class ClientHandler implements Runnable {
    private Server server;
    private RSA rsa;

    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    private Socket socket;

    private PublicKey publicKeyClient;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private Message inMessage;
    private Message outMessage;

    public ClientHandler(Socket socket, Server server) {
        rsa = new RSA();

        try {
            this.socket = socket;
            this.server = server;

            this.reader = new ObjectInputStream(socket.getInputStream());
            this.writer = new ObjectOutputStream(socket.getOutputStream());

            this.privateKey = rsa.getPrivateKey();
            this.publicKey = rsa.getPublicKey();

        } catch (EOFException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("SERVER DEBUG: Connected new user: " + socket.getInetAddress() + " : " + socket.getPort());
        exchangePublicKeys();

        outMessage = new Message("Hello");
        sendMessage(outMessage);

        while (true) {
            try {
                inMessage = (Message) reader.readObject();
            } catch (EOFException e) {
                System.out.println("SERVER DEBUG: Diconected: " + socket.getInetAddress() + " : " + socket.getPort());
                server.removeClient(this);
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Класс найти не могет");
            }

            if (!inMessage.getValue().equals("")) {
                try {
                    inMessage.setValue(rsa.decrypt(inMessage.getValue(), privateKey));
                    System.out.println(inMessage.toString());
                    server.sendMessageToAllUsers(inMessage, this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    inMessage.setValue("");
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void exchangePublicKeys() {
        inMessage = new Message(publicKey);
        try {
            writer.writeObject(inMessage);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send public key");
        }

        try {
            inMessage = (Message) reader.readObject();
            this.publicKeyClient = (PublicKey) inMessage.getObj();
        } catch (Exception e) {
        }
    }

    public void sendMessage(Message msg) {
        try {
            msg.setValue(rsa.encrypt(msg.getValue(), publicKeyClient));
            writer.writeObject(msg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send package to " + socket.getInetAddress() + " : " + socket.getPort());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SERVER DEBUG: Failed to encrypt message");
        }
    }
}
