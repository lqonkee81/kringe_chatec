package Server;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    private BasePackage pkg;

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

        while (true) {
            try {
                pkg = (BasePackage) reader.readObject();

            } catch (EOFException e) {
                System.out.println("SERVER DEBUG: Diconected: " + socket.getInetAddress() + " : " + socket.getPort());
                e.printStackTrace();
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Класс найти не могет");
            }

            if (!pkg.getValue().equals("")) {
                System.out.println(pkg.getValue());
            }

            pkg.setValue("");
        }
    }

    private void exchangePublicKeys() {
        pkg = new BasePackage(publicKey);

        try {
            writer.writeObject(pkg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot send public key");
        }

        try {
            pkg = (BasePackage) reader.readObject();
            this.publicKeyClient = (PublicKey) pkg.getObj();
            System.out.println("SERVER DEBUG: Client public key:\n" + this.publicKeyClient.toString());
        } catch (Exception e) {
        }
    }
}
