package com.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GDriveProcessor {

	private static final Logger LOG = LogManager.getLogger(GDriveProcessor.class);
	private static final String APPLICATION_NAME = "Google Sheets API Java Utility";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens-drive";

	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE_FILE);
	private static final String CREDENTIALS_FILE_PATH = "/app_credentials.json";

	private Drive driveService = null;

	public GDriveProcessor() throws GeneralSecurityException, IOException {
		createCredentialAndService();
	}

	private void createCredentialAndService() throws GeneralSecurityException, IOException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		this.driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GSheetProcessor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
				SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public String uploadFileToDrive(String filePath, String fileName, String fileMimeType) throws IOException {
		// Upload file photo.jpg on drive.
		File fileMetadata = new File();
		fileMetadata.setName(fileName);

		// File's content.
		java.io.File filePathObj = new java.io.File(filePath, fileName);
		// Specify media type and file-path for file.
		FileContent mediaContent = new FileContent(fileMimeType, filePathObj);

		try {
			File file = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
			LOG.info("Success. File uploaded with Id:{}", file.getId());
			return file.getId();
		} catch (GoogleJsonResponseException e) {
			LOG.error("Unable to upload file: " + e.getDetails());
			e.printStackTrace();
			return "";
		}
	}
}
