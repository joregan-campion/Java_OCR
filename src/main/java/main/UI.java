package main.java.main;

import main.java.database.MsAccessDatabaseConnectionInJava8;
import main.java.filePaths.FilePath;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;

public class UI extends Component {

    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    JFrame manualJFrame;
    static JTextField manualOCRTextField;
    JButton submitOCRManually;
    String manuallyEnteredJobSheet;
    private static Statement mStatment;
    private File mSelectedFile;
    private LogWindow logWindow;

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

    public void openComboBox(Statement statment) {
        logWindow = new LogWindow("Manual OCR Scanning", 500,500);
        logWindow.showInfoInLog("Scanning Database...");
        mStatment = MsAccessDatabaseConnectionInJava8.fileNameFromMsAccess();

        logWindow.showInfoInLog("Starting...");

        JFrame jFrame = new JFrame("What OCR you want to perform?");
        jFrame.setLayout(new FlowLayout());

        String[] options = {"Please Select from List", "Automatic", "Daily Work Sheet", "Service Engineer Sheets"};
        JComboBox<String> jComboBox = new JComboBox<>(options);

        JPanel jPanel = new JPanel();
        jPanel.add(jComboBox);

        jFrame.add(jPanel);
        jFrame.setSize(500, 100);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setLocation(dim.width/2-jFrame.getSize().width/2, dim.height/2-jFrame.getSize().height/2);
        jFrame.setVisible(true);

        jComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                       if (e.getItem().toString().contains("Daily") && e.getStateChange() == ItemEvent.SELECTED) {
                           System.out.println("Daily Work Sheet Selected");
                           logWindow.showInfoInLog("Daily Work Sheet Selected");
                           openImageInViewer(statment);
                       } else if (e.getItem().toString().contains("Engineer") && e.getStateChange() == ItemEvent.SELECTED) {
                           System.out.println("Service Engineer Sheet Selected");
                       } else if (e.getItem().toString().contains("Automatic") && e.getStateChange() == ItemEvent.SELECTED) {
                           System.out.println("Automatic Selected");
                           logWindow.showInfoInLog("Automatic Selected");
                           ScannedImageData.scanningDailyWorkSheets(logWindow, mStatment);
                       }
                jFrame.setVisible(false);
            }
        });

    }

    public void openImageInViewer(Statement statment) {
        JFileChooser fileChooser = new JFileChooser(new File("D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Error while processing"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        } else if (result == JFileChooser.CANCEL_OPTION) {
            fileChooser.setVisible(false);
            System.exit(0);
        }
        System.out.println("Selected File: " + selectedFile);
        logWindow.showInfoInLog("Selected File: " + selectedFile);
        Desktop desktop = Desktop.getDesktop();
        mSelectedFile = selectedFile;
        try {
            if (mSelectedFile != null) {
                desktop.open(mSelectedFile);
                fileChooser.setVisible(false);
                enterJobSheetManuallyFrame(mSelectedFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enterJobSheetManuallyFrame(File mSelectedFile) {
        manualJFrame = new JFrame("Enter Job Sheet Manually");
        submitOCRManually = new JButton("Submit");
        JButton exit = new JButton("Quit");

        manualOCRTextField = new JTextField(8);

        JPanel jPanel = new JPanel();
        jPanel.add(manualOCRTextField);
        jPanel.add(submitOCRManually);
        jPanel.add(exit);

        manualJFrame.add(jPanel);
        manualJFrame.setSize(400, 100);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        manualJFrame.setLocation(dim.width/2-manualJFrame.getSize().width/2, dim.height/2-manualJFrame.getSize().height/2);
        manualJFrame.setVisible(true);

        submitOCRManually.addActionListener(e -> {
            String enterJobSheetNumber;
            enterJobSheetNumber = manualOCRTextField.getText();
            System.out.println(enterJobSheetNumber);
            logWindow.showInfoInLog("Entered Job Sheet Number: " + enterJobSheetNumber);
            MsAccessDatabaseConnectionInJava8 databaseObject = new MsAccessDatabaseConnectionInJava8();
            String fileName = databaseObject.sqlQueryToGetDataFromWorkSheet(Integer.parseInt(enterJobSheetNumber), mStatment, logWindow);
            moveAndDeleteFileFromUI(fileName, mSelectedFile, enterJobSheetNumber, mStatment);
            manualOCRTextField.setText("");

            manualJFrame.setVisible(false);
            openImageInViewer(mStatment);
        });

        exit.addActionListener(e -> {
            System.exit(0);
        });
    }

    private void moveAndDeleteFileFromUI(String fileName, File mSelectedFile, String receiptNumber, Statement statement) {
        FilePath filePathObject = new FilePath(true);
        File file = mSelectedFile;

        MsAccessDatabaseConnectionInJava8 msAccessDB = new MsAccessDatabaseConnectionInJava8();
//        String fileNameFromDb = "Kush";
        String fileNameFromDb = "";
        try {
            fileNameFromDb = msAccessDB.sqlQueryToGetDataFromWorkSheet(Integer.parseInt(receiptNumber), statement, logWindow);
        } catch (NumberFormatException e) {
            System.out.println("ReceiptNumber : " + receiptNumber);
            System.out.println("FileName : " + fileName);
            logWindow.showInfoInLog("ReceiptNumber : " + receiptNumber);
            logWindow.showInfoInLog("FileName : " + fileName);

            if (file.renameTo(new File(filePathObject.mErrorFilePath + "\\" + fileName + ".jpg"))) {
                System.out.println("Moved in Errored File.");
                logWindow.showInfoInLog("Moved in Errored File.");
            }
            e.printStackTrace();
        }

        // Renaming the file and moving it to a new location
        byte[] bytes = null;
        try {
            bytes = ScannedImageData.compressJpgToTiff(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!fileNameFromDb.equals("")) {
            try {
                File destination = new File(filePathObject.mDestinationFilePath + "\\" + receiptNumber + "--" + fileNameFromDb  + ".tiff");
                FileOutputStream fileOutputStream = new FileOutputStream(destination);
                if (bytes != null) {
                    fileOutputStream.write(bytes);
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //double random = Math.random() * 49 + 1;
            if (file.renameTo(new File(filePathObject.mNoRecordInDatabase + "\\" + receiptNumber + ".jpg"))) {
                System.out.println("No Record found in Database.");
                logWindow.showInfoInLog("No Record found in Database. File moved in NoRecordInDatabase Folder");
            }
            System.out.println("Done");
            logWindow.showInfoInLog("Done");
        }
    }
}
