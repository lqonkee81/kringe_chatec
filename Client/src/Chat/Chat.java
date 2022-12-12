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

//    private BufferedReader reader;
//    private BufferedWriter writer;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    private String inMessage;
    private String outMessage;

    private RSA rsa;
    private PrivateKey privateKeyClient;
    private PublicKey publicKey;

    private PublicKey publicKeyServer;

    private BasePackage pkg;

    public Chat() {
        rsa = new RSA();
        privateKeyClient = rsa.getPrivateKey();
        publicKey = rsa.getPublicKey();
    }

    public void connect() throws IOException {
        this.socket = new Socket(IP_ADD, PORT);
        System.out.println("New socket");

        this.writer = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("New writer");

        this.reader = new ObjectInputStream(socket.getInputStream());
        System.out.println("New reader");

        System.out.println("Connected to server");
    }

    private void sendPackage(BasePackage pack) {
        try {
            writer.writeObject(pack);
            writer.flush();
        } catch (IOException e) {
        }
    }

    public void run() {
        exchangeKeys();

        while (true) {
        }

    }

    private void exchangeKeys() {
        try {
            pkg = (BasePackage) reader.readObject();
            this.publicKeyServer = (PublicKey) pkg.getObj();
            System.out.println("Server public key:\n" + publicKeyServer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            pkg = new BasePackage(this.publicKey);

            writer.writeObject(pkg);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
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