package main.java.main;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends JFrame {

    private int width;
    private int height;
    private JTextArea jTextArea;
    private JScrollPane jScrollPane;
    private JButton jButton;

    public LogWindow() {}

    public LogWindow(String title, int width, int height) {
        super(title);
        setSize(width, height);
        jTextArea = new JTextArea();
        jScrollPane = new JScrollPane(jTextArea);
        jButton = new JButton("Quit");
        getContentPane().add(jScrollPane);
        getContentPane().add(jButton, BorderLayout.PAGE_END);
        jButton.setPreferredSize(new Dimension(10, 50));
        jButton.addActionListener(e -> {
            System.exit(0);
        });
        setVisible(true);
    }

    public void showInfoInLog(String data) {
        jTextArea.append(data + "\n");
        this.getContentPane().validate();
    }

    public void closeLogWindow() {
        dispose();
    }
}
