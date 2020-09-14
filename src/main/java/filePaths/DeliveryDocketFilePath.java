package main.java.filePaths;

public class DeliveryDocketFilePath {


    public String mInitialFilePath;
    public String mDestinationFilePath;
    public String mErroredFilePath;

    public String languageDataPath;

    public DeliveryDocketFilePath(boolean isProduction) {

        languageDataPath = "D:\\Program Files\\Workspace\\Tess4J";

        if (isProduction) {
            mInitialFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Counter Sales\\pre-filing";
            mDestinationFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Counter Sales\\Processing Documents";
            mErroredFilePath = "D:\\Dropbox\\Dropbox (Campion Pumps)\\Campion Backup Folder (1)\\Scanned Documents\\Counter Sales\\Errored Documents";
        } else {
            mInitialFilePath = "D:\\OCR\\Docket Documents\\Initial Documents";
            mDestinationFilePath = "D:\\OCR\\Docket Documents\\Processing Documents";
            mErroredFilePath = "D:\\OCR\\Docket Documents\\Errored Documents";
        }

    }
}
