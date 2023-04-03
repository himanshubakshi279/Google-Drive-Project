package com.utility;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class ApplicationMain {
	Drive drive ;
	private static final Logger LOG = LogManager.getLogger(ApplicationMain.class);

	public static void main(String[] args) throws IOException {
		// Read configuration file and load properties
		String sheetId = AppProperties.getInstance().getSpreadSheetId();
		String folderId = AppProperties.getInstance().getDriveId();
		ApplicationMain app = new ApplicationMain();

		// Test Google Sheet API
		//app.testGoogleSheetAPI(sheetId);

		// Test Google Drive API
		app.testGoogleDriveAPI();
		//app.uploadFile();

	}
	
	private void testGoogleDriveAPI() throws IOException {
		
		String filePath = "C:\\Users\\hp\\Desktop";
		String fileName = "1840_872.jpg";
		String fileMimeType = "image/jpeg";

		try {
			GDriveProcessor driveProcessor = new GDriveProcessor();
			driveProcessor.uploadFileToDrive(filePath, fileName, fileMimeType);
		} catch (GeneralSecurityException e) {
			LOG.error("GeneralSecurityException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void testGoogleSheetAPI(String sheetId) {
		final String range = "Load_Time_tracking!A1:T";

		try {
			GSheetProcessor processor = new GSheetProcessor();

			// Reading from GoogleSheet
			List<List<Object>> values = processor.readGoogleSheet(sheetId, range);

			// Updating second column with new Values
			for (List<Object> value : values) {
				value.add(1, "Testing-2");
			}

			// Updating Google Sheet
			processor.updateGoogleSheet(sheetId, range, values);

		} catch (GeneralSecurityException e) {
			LOG.error("GeneralSecurityException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
