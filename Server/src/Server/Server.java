package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Package.*;

public class Server {
    private final int PORT = 8721;

    private ArrayList<ClientHandler> allClients = new ArrayList<ClientHandler>();
    private ServerSocket sSoket;

    public Server() {
        try {
            sSoket = new ServerSocket(PORT);
            System.out.println("SERVER DEBUG: Server is started on " + PORT + " port");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        while (true) {
            Socket socket;
            try {
                socket = sSoket.accept();
                ClientHandler client = new ClientHandler(socket, this);
                allClients.add(client);

                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler client) {
        allClients.remove(client);
    }

    public void sendMessageToAllUsers(Message msg, ClientHandler self) {
        for (ClientHandler client : allClients) {
            if (client != self) {
                System.out.println("SERVER DEBUG: Trying send message to all users");

                client.sendMessage(msg);
            }
        }
    }
}