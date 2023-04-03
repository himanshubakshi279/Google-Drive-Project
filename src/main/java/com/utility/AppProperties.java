package com.utility;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppProperties {

	private static final Logger LOG = LogManager.getLogger(AppProperties.class);
	private static AppProperties appProp = null;
	private String spreadSheetId;
	private String DriveID;

	// Singleton
	private AppProperties() {
	}

	public static AppProperties getInstance() {
		if (appProp == null) {
			LOG.info("Creating AppProperties Object...");
			appProp = new AppProperties();
			InputStream in = null;
			Properties prop = new Properties();

			try {
				in = AppProperties.class.getClassLoader().getResourceAsStream("config.properties");
				prop.load(in);

				// Retrieve value from config.properties file
				appProp.setSpreadSheetId(prop.getProperty("spreadSheetId"));
				appProp.setDriveId(prop.getProperty("DriveID"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return appProp;
	}

	public String getSpreadSheetId() {
		return spreadSheetId;
	}

	public void setSpreadSheetId(String spreadSheetId) {
		this.spreadSheetId = spreadSheetId;
	}
	
	public String getDriveId() {
		return DriveID;
	}

	public void setDriveId(String drive) {
		this.DriveID = drive;
	}
}
