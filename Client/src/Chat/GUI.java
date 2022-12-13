package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private JButton btnSendMessage = new JButton("➡");
    private JButton btnDisconnect = new JButton("Отлючиться от чата");
    private JTextArea textArea = new JTextArea();

    private GridLayout layout = new GridLayout(2, 2, 10, 10);

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private final int screenHeight;
    private final int screenWight;
    private final int height = 600;
    private final int wight = 800;

    public GUI() {
        screenHeight = screenSize.height;
        screenWight = screenSize.width;

        this.setTitle("Kringe chatec");
        this.setBounds((screenWight / 2) - (wight / 2), (screenHeight / 2) - (height / 2), wight, height);
//        textArea.setFocusable(false);

        Container container = this.getContentPane();
        container.setLayout(layout);

        container.add(textArea);
        container.add(btnSendMessage);
        container.add(btnDisconnect, -1);

        btnSendMessage.addActionListener(new SendMessageButtonListener());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Buttons events
    class SendMessageButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = textArea.getText();
            if (msg.isEmpty()) {
                System.out.println("null");
            } else {
                System.out.println(msg);
                textArea.setText("");
            }
        }
    }
}