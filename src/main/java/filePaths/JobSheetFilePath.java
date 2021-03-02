package main.java.filePaths;

public class JobSheetFilePath {

	public String mNPEFilePath;

	public  String mNoRecordInDatabase;

	public String dataPath;

	public String mFilePath;

	public String mInitialFilePath;

	public String mDestinationFilePath;

	public String mDatabaseLocation;

	public String mErrorFilePath;

	public String mProcessedCompressedFilePath;

	public String languageDataPath;

	public JobSheetFilePath(boolean isProduction) {
		
		dataPath = "D:/OCR";

		languageDataPath = "D:\\Program Files\\Workspace\\Tess4J";

		mDatabaseLocation = "D:/OCR/ControlDataBase.mdb";

		if (isProduction) {
			mFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Pre-Filing";	//Pre-Filing

			mInitialFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Pre-Filing"; //Pre-Filing

			mDestinationFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Processed_doc_after_24_02_21";

			mNPEFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\FileCausingNPE";

			mErrorFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\Error while processing";

			mNoRecordInDatabase = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Worksheets\\No Record In Database";

		} else {
			/* Give NPE fake location */
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


