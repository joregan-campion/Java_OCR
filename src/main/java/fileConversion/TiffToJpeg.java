package main.java.fileConversion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://stackoverflow.com/questions/15429011/how-to-convert-tiff-to-jpeg-png-in-java
public class TiffToJpeg {

    public static String mTiffFiles = "D:\\OCR\\fileConversion\\tiff";
    public static String mJpegFiles = "D:\\OCR\\fileConversion\\jpeg";

    public static void main(String[] args) {

        List<String> listOfFileName = scanFolderAndGetAllDocumentName();

        convertTiffToJpeg(listOfFileName);

//        convertJpegToTiff(listOfFileName);
    }

    private static void convertJpegToTiff(List<String> listOfFileName) {
        for (String fileName : listOfFileName) {
            final BufferedImage tif;
            try {
                tif = ImageIO.read(new File(mTiffFiles + "\\" + fileName));
                ImageIO.write(tif, "jpg", new File(mJpegFiles + "\\" + fileName + ".tiff"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void convertTiffToJpeg(List<String> listOfFileName) {

        for (String fileName : listOfFileName) {
            final BufferedImage tif;
            try {
                tif = ImageIO.read(new File(mTiffFiles + "\\" + fileName));
                ImageIO.write(tif, "jpg", new File(mJpegFiles + "\\" + fileName + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> scanFolderAndGetAllDocumentName() {

        File folder = new File(mTiffFiles);
        File[] listOfFiles = folder.listFiles();

        List<String> fileName = new ArrayList<String>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileName.add(file.getName());
            }
        }
        return fileName;
    }
}
