package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;

import Package.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;              // Объект запущенного сервера ( используеься только для ретрансляции полученного пакета всем пользователям )

    private ObjectOutputStream writer;  // Поток для отправки пакета пользователю
    private ObjectInputStream reader;   // Поток для чтения полученного пакета от пользователя


    private RSA rsa;                    // Шифрование
    private PublicKey publicKeyClient;  // Публичный ключ полученный от клиента
    private PublicKey publicKey;        // Публичный ключ сервера
    private PrivateKey privateKey;      // Приватный ключ сервера

    private Message inMessage;          // Полученный пакет от пользователя
    private Message outMessage;         // Отправляемый пакет пользвателю ( не используется )
    private String nickname;            // Ник пользователя

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
        /*
         * Переобределение этого метода необходимо для того, чтобы каждый клиент обслуживался в отдельном потоке
         */

        System.out.println("SERVER DEBUG: Connected new user: " + socket.getInetAddress() + " : " + socket.getPort());
        exchangePublicKeys();
        preparing();
        sayHello();

        while (true) {
            try {
                inMessage = (Message) reader.readObject();
            } catch (EOFException | SocketException e) {
                System.out.println("SERVER DEBUG: Diconected: " + socket.getInetAddress() + " : " + socket.getPort());
                server.removeClient(this);
                break;
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Класс найти не могет");
            }

            if (!inMessage.getValue().equals("")) {
                try {
                    inMessage = rsa.decrypt(inMessage, privateKey);
                    System.out.println(inMessage.toString());
                    server.sendMessageToAllUsers(inMessage);
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

    private void preparing() {
        /*
         * Тут серверу прилдетает никнейм пользователя
         */

        try {
            inMessage = (Message) reader.readObject();
            inMessage = rsa.decrypt(inMessage, privateKey);
            this.nickname = inMessage.getNickname();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exchangePublicKeys() {
        /*
         * Обмен публичными ключами пользователя с сервером
         *
         * Сначала сервер отправляет свой ключ. Затем принимает ключ пользователя
         */

//        inMessage = new Message(publicKey);
        try {
            writer.writeObject(publicKey);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send public key");
        }

        try {
//            inMessage = (Message) reader.readObject();
            this.publicKeyClient = (PublicKey) reader.readObject();
            System.out.println("Getting public key from klient");
            System.out.println(publicKeyClient.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sayHello() {
        /*
         * Тут просто сервер говорит пользователю 'Hello' в качестве подтверждения, что соединение установлено
         */

        Message msg = new Message("Hi, " + nickname, "Server");
        try {
            msg = rsa.encrypt(msg, publicKeyClient);
            writer.writeObject(msg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send package to " + socket.getInetAddress() + " : " + socket.getPort());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SERVER DEBUG: Failed to encrypt message");
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        /*
         * Отправка сообщение пользователю
         */

        try {
            msg = rsa.encrypt(msg, publicKeyClient);
            writer.writeObject(msg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send package to " + socket.getInetAddress() + " : " + socket.getPort());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SERVER DEBUG: Failed to encrypt message");
            e.printStackTrace();
        }
    }
}
