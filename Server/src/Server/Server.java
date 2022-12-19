package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Package.*;

public class Server {
    private final int PORT = 8721;

    private ArrayList<ClientHandler> allClients = new ArrayList<ClientHandler>(); // Массив со всеми клиентами
    private ServerSocket sSoket;

    private final String PATH_TO_LOGS_DIRECTORY = "./Server/logs/"; // Путь до директории с логами

    private Message messageForAllUsers;

    public Server() {
        try {
            sSoket = new ServerSocket(PORT);
            System.out.println("SERVER DEBUG: Server is started on " + PORT + " port");
        } catch (IOException e) {
            System.out.println("SERVER DEBUG: Cannot access user");
            e.printStackTrace();
        }
    }

    public void run() {
        /* Здесь происхоидит магия с сокетами. Тобишь устанавливается содеинение с клиентом
         * Клиент обрабатывается в классе ClientHandler. Сервер хранит всех клиентов в массиве *
         */
        while (true) {
            Socket socket;
            try {
                socket = sSoket.accept();
                ClientHandler client = new ClientHandler(socket, this);
                allClients.add(client);

                // Каждый клиент обрабатывается в отдельном потоке
                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientHandler client) {
        /*
         * Удаление пользователя из массива при дисконекте
         */

        allClients.remove(client);
    }

    public synchronized void sendMessageToAllUsers(Message msg) {
        /*
         * Отправка сообщения всем пользователям, которое прилетело на сервер
         */

        logging(msg);
//        Message messageForAllUsers = new Message(msg);

        for (ClientHandler client : allClients) {
            try {
                messageForAllUsers = msg.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            client.sendMessage(messageForAllUsers);
        }
    }

    private void logging(Message msg) {
        /* Запись лога переписки в файл
         * По хорошему нужно написать отдельный класс логера
         */

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());

        // Путь до файла с логом
        String pathLogFile = PATH_TO_LOGS_DIRECTORY + "log_" + formatter.format(date) + ".log";

        File f = new File(pathLogFile);

        try {
            // Если файла с логом не существует, то он создаётся ( под виндой может не работать )
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