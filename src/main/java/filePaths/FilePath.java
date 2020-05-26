package main.java.filePaths;

public class FilePath {

	public  String mNoRecordInDatabase;

	public String dataPath;

	public String mFilePath;

	public String mInitialFilePath;

	public String mDestinationFilePath;

	public String mDatabaseLocation;

	public String mErrorFilePath;

	public String mProcessedCompressedFilePath;

	public String languageDataPath;

	public FilePath(boolean isProduction) {
		
		dataPath = "D:/OCR";

		languageDataPath = "D:\\Program Files\\Workspace\\Tess4J";

		mDatabaseLocation = "D:/OCR/ControlDataBase.mdb";

		if (isProduction) {
			mFilePath = "C:\\Users\\kushal.mishra\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Pre-Filing";

			mInitialFilePath = "C:\\Users\\kushal.mishra\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Pre-Filing";

			mDestinationFilePath = "C:\\Users\\kushal.mishra\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Processing Documents";
			
			mErrorFilePath = "C:\\Users\\kushal.mishra\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Error while processing";

			mNoRecordInDatabase = "C:\\Users\\kushal.mishra\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\No Record In Database";

		} else {
			mFilePath = "D:/OCR/InitialFolder";
			
			mInitialFilePath = "D:\\OCR\\InitialFolder";

			mDestinationFilePath = "D:\\OCR\\destinationFolder";
			
			mErrorFilePath = "D:\\OCR\\Error";

			mNoRecordInDatabase = "D:\\OCR\\No Record In Database";

		}
		
	}
		
	
}

//public String filePath = "D:/OCR/InitialFolder";

//public String initialFilePath = "D:\\OCR\\InitialFolder";

//public String destinationFilePath = "D:\\OCR\\destinationFolder";


