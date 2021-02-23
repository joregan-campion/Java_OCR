package main.java.main;

import java.io.*;

public class CopyDatabaseForProcessing {

    public CopyDatabaseForProcessing() {

        File source =  new File("W:\\ControlDataBase.mdb");
        File destination =  new File("D:\\OCR\\ControlDataBase.mdb");
        /*
        // For Testing
        File source =  new File("D:\\OCR\\ControlDataBase.mdb");
        File destination =  new File("D:\\OCR\\fromInitial\\ControlDataBase.mdb");
        */

        copyFileUsingJava7Files(source, destination);

    }

    private static void copyFileUsingJava7Files(File source, File dest) {
        try {
//            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            copyFileUsingStream(source, dest);
            System.out.println("File Copied..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
}
