package main.java.main;

import main.java.database.MsAccessDatabaseConnectionInJava8;
import main.java.filePaths.FilePath;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//    https://www.geeksforgeeks.org/tesseract-ocr-with-java-with-examples/
public class ScannedImageData {

    static FilePath filePathObject = new FilePath(true);   //true
    private static Statement mStatment;

    public static void main(String args[]) throws Exception
    {
        mStatment = MsAccessDatabaseConnectionInJava8.fileNameFromMsAccess();

        manualOCRDailyWorkSheet();
//        scanningDailyWorkSheets();

    }

    private static void manualOCRDailyWorkSheet() {
        UI allUIFile = new UI();
        File openIndividualImage = allUIFile.openImageInViewer(mStatment);
    }

    private static void scanningDailyWorkSheets() {
        List<String> listOfFileName = scanFolderAndGetAllDocumentName();

//        Statement statement = null;
        try {
            scanForDocumentNumber(listOfFileName, mStatment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scanForDocumentNumber(List<String> listOfFileName, Statement statement) {

        List<Integer> receiptNumberList = new ArrayList<>();
        int convertingToInt = 0;
        try {
            for (String fileName : listOfFileName) {

                File file = new File(filePathObject.mInitialFilePath + "\\" + fileName);

                BufferedImage ipimage = ImageIO.read(file);

                if (file.getName().equals("desktop.ini")) {
                    continue;
                }

                // getting RGB content of the whole image file
                double d = 0.0;
                try {
                    d = ipimage
                                .getRGB(ipimage.getTileWidth() / 2,
                                        ipimage.getTileHeight() / 2);
                } catch (NullPointerException e) {
                    System.out.println("" + file.getName().toString());
                    e.printStackTrace();
                }

                // comparing the values
                // and setting new scaling values
                // that are later on used by RescaleOP
                if (d >= -1.4211511E7 && d < -7254228) {
                    processImg(ipimage, 3f, -10f, fileName, statement);
                } else if (d >= -7254228 && d < -2171170) {
                    processImg(ipimage, 1.455f, -47f, fileName, statement);
                } else if (d >= -2171170 && d < -1907998) {
                    processImg(ipimage, 1.35f, -10f, fileName, statement);
                } else if (d >= -1907998 && d < -257) {
                    processImg(ipimage, 1.19f, 0.5f, fileName, statement);
                } else if (d >= -257 && d < -1) {
                    processImg(ipimage, 1f, 0.5f, fileName, statement);
                } else if (d >= -1 && d < 2) {
                    processImg(ipimage, 1f, 0.35f, fileName, statement);
                } else if (d >= -1.8380756E7 && d < -1.4211511E7) {
                    processImg(ipimage, 3f, -10f, fileName, statement);
                } else {
                    processImg(ipimage, 3f, -10f, fileName, statement);
                }
            }
        }
        catch (TesseractException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void processImg(BufferedImage ipimage, float scaleFactor, float offset,
                                  String fileName, Statement statement) throws IOException, TesseractException  {
        // Making an empty image buffer
        // to store image later
        // ipimage is an image buffer
        // of input image
        BufferedImage opimage
                = new BufferedImage(1050,
                1024,
                ipimage.getType());

        // creating a 2D platform
        // on the buffer image
        // for drawing the new image
        Graphics2D graphic
                = opimage.createGraphics();

        // drawing new image starting from 0 0
        // of size 1050 x 1024 (zoomed images)
        // null is the ImageObserver class object
        graphic.drawImage(ipimage, 0, 0,1050, 1024, null);
        graphic.dispose();

        // rescale OP object
        // for gray scaling images
        RescaleOp rescale
                = new RescaleOp(scaleFactor, offset, null);

        // performing scaling
        // and writing on a .png file
        BufferedImage fopimage
                = rescale.filter(opimage, null);

//        double random = Math.random() * 49 + 1;
        /*ImageIO.write(fopimage,"jpg",
                        new File(filePathObject.mProcessedCompressedFilePath + "\\" + ".tiff"));*/

        // Instantiating the Tesseract class
        // which is used to perform OCR
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(filePathObject.languageDataPath);

        // doing OCR on the image
        // and storing result in string text
        String text = tesseract.doOCR(fopimage);

        String receiptNumber = "";
        Pattern pattern = Pattern.compile("\\d{6,}");
        Matcher matcher = pattern.matcher(text.substring(0, 80));
        if(matcher.find()) {
            receiptNumber = matcher.group();
        }

        System.out.println(receiptNumber);
        moveAndDeleteFile(fileName, receiptNumber, statement);
    }

    public static void moveAndDeleteFile(String fileName, String receiptNumber, Statement statement) {

        // Giving the initial file path
        File file = new File(filePathObject.mInitialFilePath + "\\" + fileName);

        MsAccessDatabaseConnectionInJava8 msAccessDB = new MsAccessDatabaseConnectionInJava8();
//        String fileNameFromDb = "Kush";
        String fileNameFromDb = "";
        try {
            fileNameFromDb = msAccessDB.sqlQueryToGetDataFromWorkSheet(Integer.parseInt(receiptNumber), statement);
        } catch (NumberFormatException e) {
            System.out.println("ReceiptNumber : " + receiptNumber);
            System.out.println("FileName : " + fileName);

            if (file.renameTo(new File(filePathObject.mErrorFilePath + "\\" + fileName + ".jpg"))) {
                System.out.println("Moved in Errored File.");
            }
            e.printStackTrace();
        }

        // Renaming the file and moving it to a new location
        byte[] bytes = null;
        try {
            bytes = compressJpgToTiff(file);
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
            if (file.renameTo(new File(filePathObject.mErrorFilePath + "\\" + receiptNumber + ".jpg"))) {
                System.out.println("Moved in Errored File.");
            }
            System.out.println("No Record found in Database.");
        }
    }

    private static List<String> scanFolderAndGetAllDocumentName() {

        File folder = new File(filePathObject.mInitialFilePath);
        File[] listOfFiles = folder.listFiles();

        List<String> fileName = new ArrayList<String>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileName.add(file.getName());
                // System.out.println(file.getName());
            }
        }
        return fileName;
    }

    public static byte[] compressJpgToTiff(File imageFile) throws Exception {
        //Add rest of your method code here
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(255);
        ImageOutputStream imageOutputStream = null;
        try {
            File input = new File(imageFile.getAbsolutePath());

            Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = imageWriterIterator.next();
            imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
            writer.setOutput(imageOutputStream);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("JPEG");
            param.setCompressionQuality(0.1f);

            BufferedImage bufferedImage = ImageIO.read(input);
            writer.write(null, new IIOImage(bufferedImage, null, null), param);
            writer.dispose();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (imageOutputStream != null)
                imageOutputStream.close();
            byteArrayOutputStream.close();
        }
    }

}
