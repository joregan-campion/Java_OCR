package main.java.main;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import main.java.database.MsAccessDatabaseConnectionInJava8;
import main.java.filePaths.FilePath;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ReadingOCR {


	static FilePath filePathObject = new FilePath(false);

	static MsAccessDatabaseConnectionInJava8 msAccessDB = new MsAccessDatabaseConnectionInJava8();

	public static void main(String[] args) {

		List<String> listOfFileName = scanFolderAndGetAllDocumentName();
		
//		Statement statement = MsAccessDatabaseConnectionInJava8.fileNameFromMsAccess();
		Statement statement = null;

		scanDocumentAndDocumentNumber(listOfFileName, statement);
		
		//openFileDialog();
	}

	private static void openFileDialog() {
		
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		//fileChooser.setCurrentDirectory(new File(System.getProperty(urls.initialFilePath)));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = fileChooser.showOpenDialog(null);
		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
		}
		System.out.println("" + selectedFile);
		
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

	private static void scanDocumentAndDocumentNumber(List<String> listOfFileName, Statement statement) {
		Tesseract tesseract = new Tesseract();
		
		//tesseract.setTessVariable("tessedit_char_whitelist", "0123456789abcdefghijklmnopqrstuvwxyz");
		List<Integer> receiptNumberList = new ArrayList<>();
		int convertingToInt = 0;
		try {
			tesseract.setDatapath(filePathObject.dataPath);

			for (int i = 0; i < listOfFileName.size(); i++) {
				// the path of your tess data folder inside the extracted file
				String text = tesseract.doOCR(new File(filePathObject.mFilePath + "/" + listOfFileName.get(i)));

				// String receiptNumber = text.substring(35, 40);	// Service Engineer Report Sheet
				// TODO two types of Daily work sheet, keep in mind, so on
				// NumberFormatException check for different OCR
				//String receiptNumber = text.substring(17, 23);	// Daily Work Sheet //Working COde
				
				//if(text.substring(0, 50).contains("DAILY")) {
					String receiptNumber = "";
					Pattern pattern = Pattern.compile("\\d{6,}");
					Matcher matcher = pattern.matcher(text.substring(0, 80)); 
					if(matcher.find()) {
						receiptNumber = matcher.group();
					}
									
					System.out.println(receiptNumber);
					  try { 
						  convertingToInt = Integer.parseInt(receiptNumber);
						  									  
						  moveAndDeleteFile(listOfFileName.get(i), receiptNumber, statement);
					  } catch (NumberFormatException ex) {
						  ex.printStackTrace();
					  }
					 
					receiptNumberList.add(convertingToInt);
				/*} else {
					File file = new File(filePathObject.mInitialFilePath + "\\" + listOfFileName.get(i));
					System.out.println("Not a daily report - Wrong Report");
					if (file.renameTo(new File(filePathObject.mErrorFilePath + "\\" + listOfFileName.get(i) + ".jpg"))) {
						System.out.println("Moved To Error Folder - Wrong Report");
					}
				}*/
			}
		} 
		catch (TesseractException e) {
			e.printStackTrace();
		} 
	}

	private static void moveAndDeleteFile(String fileName, String receiptNumber, Statement statement) {

		// Giving the initial file path
		File file = new File(filePathObject.mInitialFilePath + "\\" + fileName);
		 
		MsAccessDatabaseConnectionInJava8 msAccessDB = new MsAccessDatabaseConnectionInJava8();
		String fileNameFromDb = "Kush";
//		String fileNameFromDb = msAccessDB.sqlQueryToGetDataFromWorkSheet(Integer.parseInt(receiptNumber), statement);

		// Renaming the file and moving it to a new location
		if (!fileNameFromDb.equals("")) { 
			
			byte[] bytes;
			
		    try {
		    	bytes = compressJpgToTiff(file);
			    File destination = new File(filePathObject.mDestinationFilePath + "\\" + receiptNumber + "--" + fileNameFromDb  + ".tiff");
			    FileOutputStream fileOutputStream = new FileOutputStream(destination);
				fileOutputStream.write(bytes);
				file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    
//			if (file.renameTo(new File(filePathObject.mDestinationFilePath + "\\" + receiptNumber + "--" + fileNameFromDb + ".tiff"))) {
//	
//				System.out.println("File moved successfully");
//				//JOptionPane.showMessageDialog(null, "File moved successfully");
//			} else {
//				System.out.println("Failed to move the file.");
//			}
		} else {
			//double random = Math.random() * 49 + 1;
			if (file.renameTo(new File(filePathObject.mErrorFilePath + "\\" + receiptNumber + ".jpg"))) {
				System.out.println("Moved in Errored File.");
			}
			System.out.println("No Record found in Database.");
		}
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
