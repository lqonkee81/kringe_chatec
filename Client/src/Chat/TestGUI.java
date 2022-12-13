package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.StreamCorruptedException;
import java.util.TreeMap;

import Package.*;

public class TestGUI extends JFrame {
    private Chat chat;

    private JTextArea sendingMessageField;  // Поле для ввода отправляемого сообщения
    private JTextArea messagesOutArea;      // Поле в которое прилетают сообщения с сервера ( лог переписки )

    private JButton btnAutorize;            // Кнопка атворизации/регистрации
    private JButton btnSendMessage;          // Кнопка отправки сообщения

    JScrollPane scrollPaneMessagesOutArea;  // Скрол для поля с логом переписки

    JPanel bottomPanel;                     // Нижняя панель с кнопками авторизации, отправки сообщения и полем для ввода сообщения

    // Авторизационные данные пользователя
    private String nickname;
    private char[] password;


    public void setTextArea(Message msg) {
        this.messagesOutArea.setText(msg.toString());
    }

    // конструктор
    public TestGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWight = screenSize.width;
        int screenHeight = screenSize.height;

        int wight = 800;
        int height = 600;


        /*============== Задаём настройки главного окна ==============*/
        this.setBounds((screenWight / 2) - (wight / 2), (screenHeight / 2) - (height / 2), wight, height);
        this.setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        /*============== Задаем настройки поля с логом переписки ==============*/
        messagesOutArea = new JTextArea();
        messagesOutArea.setEditable(false);
        messagesOutArea.setLineWrap(true);

        /*============== Создаем скролл для понели с логом переписки ==============*/
        scrollPaneMessagesOutArea = new JScrollPane(messagesOutArea);

        /*============== Создаем нижнюю панель и добавляем на нее элементы ==============*/
        bottomPanel = new JPanel(new BorderLayout());
        btnSendMessage = new JButton("Отправить");
        btnAutorize = new JButton("Авторизация");
        sendingMessageField = new JTextArea("Введите ваше сообщение: ");

        bottomPanel.add(btnSendMessage, BorderLayout.EAST);
        bottomPanel.add(sendingMessageField, BorderLayout.CENTER);
        bottomPanel.add(btnAutorize, BorderLayout.WEST);

        /*============== Добавляем в главную форму все скомпанованные элементы ==============*/
        this.add(scrollPaneMessagesOutArea, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.setVisible(true);




        btnAutorize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Autorize");
                JTextField loginField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                JButton btnOk = new JButton("ok");

                GridLayout gridLayout = new GridLayout(3, 1, 15, 15);
                Container container = f.getContentPane();
                container.setLayout(gridLayout);

                f.setSize(200, 200);

                loginField.setText("");
                passwordField.setText("");

                container.add(loginField);
                container.add(passwordField);
                container.add(btnOk);

                f.setVisible(true);

                btnOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nickname = loginField.getText();
                        password = passwordField.getPassword();

                        chat = new Chat(nickname);
                        try {
                            chat.connect();
                            chat.run();
                        } catch (Exception ex) {
                            System.out.println("Cannot connect to servcer");
                            ex.printStackTrace();
                        }
                        f.setVisible(false);


                        /*============== Запускаем прослушку сообщений в отдельном потоке ==============*/
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg;
                                while (true) {
                                    try {
                                        msg = chat.getMessage();
                                        messagesOutArea.setText(msg.toString());
                                    } catch (NullPointerException | StreamCorruptedException ignored) {}
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();


                    }
                });
            }
        });

        btnSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message msg = new Message(sendingMessageField.getText(), nickname);
                chat.sendMessage(msg);
            }
        });

        // при фокусе поле сообщения очищается
        sendingMessageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                sendingMessageField.setText("");
            }
        });
    }
}
