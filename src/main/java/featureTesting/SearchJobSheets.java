package main.java.featureTesting;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchJobSheets {

    private static final String ACCESS_TOKEN = "OheyhveXP4AAAAAAAAAtSQqWF0PhU8AFGRdl1GtO1K0YI5eC20ljCebVTFqh7tJK";
//    private static final String ACCESS_TOKEN = "OheyhveXP4AAAAAAAAAtTeginMXCli3FKetXpSwN1qm3L9ABEvCNz8_Zlv8SZ_A5";

    public static void main(String[] args) {

        DbxRequestConfig dbxRequestConfig =
                DbxRequestConfig.newBuilder("https://www.dropbox.com/home/Campion%20Backup%20Folder%20(1)/Scanned%20Documents/Worksheets/Error%20while%20processing").build();
        DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig, ACCESS_TOKEN);

        // Get Current account information
        try {
            FullAccount account = dbxClientV2.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());

            syncFiles(dbxClientV2);

        } catch (DbxException e) {
            e.printStackTrace();
        }

    }

    public static String syncFiles(DbxClientV2 client) throws ListFolderErrorException, DbxException {

        ListFolderBuilder listFolderBuilder = client.files().listFolderBuilder("");
        ListFolderResult result = listFolderBuilder.withRecursive(true).start();

        Logger log = Logger.getLogger("thread");
        log.setLevel(Level.INFO);

        while (true) {

            if (result != null) {
                for ( Metadata entry : result.getEntries()) {
                    if (entry instanceof FileMetadata){
                        log.info("Added file: "+entry.getPathLower());
                    }
                }

                if (!result.getHasMore()) {
                    log.info("GET LATEST CURSOR");
                    return result.getCursor();
                }

                try {
                    result = client.files().listFolderContinue(result.getCursor());
                } catch (DbxException e) {
                    log.info ("Couldn't get listFolderContinue");
                }
            }
        }
    }
}
