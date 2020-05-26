package main.java.main;

import main.java.database.MsAccessDatabaseConnectionInJava8;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Statement;

public class UI extends JFrame implements ActionListener {

    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    JFrame manualJFrame;
    static JTextField manualOCRTextField;
    JButton submitOCRManually;
    String manuallyEnteredJobSheet;
    private static Statement mStatment;

    UI() {}

    public File openFileSourceDialog() {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }
        System.out.println("" + selectedFile);
        return selectedFile;
    }

    public File openImageInViewer(Statement statment) {
        mStatment = statment;
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        } else if (result == JFileChooser.CANCEL_OPTION) {
        }
        System.out.println("" + selectedFile);
        Desktop desktop = Desktop.getDesktop();
        try {
            if (selectedFile != null) {
                desktop.open(selectedFile);
                enterJobSheetManuallyFrame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selectedFile;
    }

    private void enterJobSheetManuallyFrame() {
        UI uIObject = new UI();
        manualJFrame = new JFrame("Enter Job Sheet Manually");
        submitOCRManually = new JButton("Submit");

        submitOCRManually.addActionListener(uIObject);

        manualOCRTextField = new JTextField(8);

        JPanel jPanel = new JPanel();
        jPanel.add(manualOCRTextField);
        jPanel.add(submitOCRManually);

        manualJFrame.add(jPanel);
        manualJFrame.setSize(400, 100);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        manualJFrame.setLocation(dim.width/2-manualJFrame.getSize().width/2, dim.height/2-manualJFrame.getSize().height/2);
        manualJFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String submitOCR = e.getActionCommand();
        String enterJobSheetNumber;
        if (submitOCR.equals("Submit")) {
            enterJobSheetNumber = manualOCRTextField.getText();
            System.out.println(enterJobSheetNumber);
            MsAccessDatabaseConnectionInJava8 databaseObject = new MsAccessDatabaseConnectionInJava8();
            String fileName = databaseObject.sqlQueryToGetDataFromWorkSheet(Integer.parseInt(enterJobSheetNumber), mStatment);
            ScannedImageData.moveAndDeleteFile(fileName, enterJobSheetNumber, mStatment);
            manualOCRTextField.setText("");
        }
    }
}
