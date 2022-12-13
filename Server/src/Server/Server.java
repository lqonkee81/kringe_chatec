package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Package.*;

public class Server {
    private final int PORT = 8721;

    private ArrayList<ClientHandler> allClients = new ArrayList<ClientHandler>();
    private ServerSocket sSoket;

    private final String PATH_TO_LOGS_DIRECTORY = "./Server/logs/";

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

    public void sendMessageToAllUsers(Message msg) {
        logging(msg);
        for (ClientHandler client : allClients) {
            client.sendMessage(msg);
        }
    }

    private void logging(Message msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());

        String pathLogFile = PATH_TO_LOGS_DIRECTORY + "log_" + formatter.format(date) + ".log";

        File f = new File(pathLogFile);

        try {
            if (f.createNewFile()) {
                System.out.println("SERVER DEBUG: Created new log file: " + pathLogFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter(pathLogFile, true)) {
            fileWriter.write(msg.toString() + "\n");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }
    }
}