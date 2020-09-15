package tests.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import main.java.breakingPointTool.connection.DatabaseConnection;
import main.java.breakingPointTool.connection.SonarDatabaseConnection;
import main.java.breakingPointTool.main.BreakingPointTool;

public class fileConfiguration 
{
	private String usernameSQ = "";
	private String passwordSQ = "";
	// For results database
	private String usernameDBConnection = "";
	private String passwordDBConnection= "";
	private String sonarName = "";
	private String serverName = "";
	private String dbName = "";
	
	public void readConfigurationFile() throws IOException
	{
		String line;

		java.net.URL jarLocationUrl = BreakingPointTool.class.getProtectionDomain().getCodeSource().getLocation();
		String jarLocation = new File(jarLocationUrl.toString()).getParent();
		jarLocation = jarLocation.replace("file:", "");

		int t = jarLocation.lastIndexOf("/");
		jarLocation = jarLocation.substring(0,t);

		if (new File(jarLocation + "/configurations.txt").exists()) 
		{ 
			BufferedReader br = new BufferedReader(new FileReader(jarLocation + "/configurations.txt"));

			while ((line = br.readLine()) != null)
			{ 
				if (line.contains("#")) 
					continue;

				// SonarQube Database Credentials
				if (line.contains("usernameSQDB:")) 
				{ 
					String[] temp = line.split("usernameSQDB:"); 
					this.usernameSQ = temp[1]; 
				}

				if (line.contains("passwordSQDB:")) 
				{  
					String[] temp = line.split("passwordSQDB:"); 
					this.passwordSQ = temp[1];
				} 

				// Metrics Database Credentials
				if (line.contains("username=")) 
				{ 
					String[] temp = line.split("username="); 
					this.usernameDBConnection = temp[1]; 
				}

				if (line.contains("password=")) 
				{  
					String[] temp = line.split("password="); 
					this.passwordDBConnection = temp[1];
				} 

				if (line.contains("sonarName:"))
				{
					String[] temp = line.split("sonarName:");
					this.sonarName = temp[1];
				}

				//  Metrics DB credentials
				if (line.contains("serverName="))
				{
					String[] temp = line.split("serverName=");
					this.serverName = temp[1];
				}

				if (line.contains("databaseName="))
				{
					String[] temp = line.split("databaseName=");
					this.dbName = temp[1];
				}
 
			}
			br.close();
			// Set Credentials to Database
			new SonarDatabaseConnection(this.usernameSQ, this.passwordSQ, this.sonarName);
			new DatabaseConnection(this.usernameDBConnection, this.passwordDBConnection, this.serverName + "/" +  this.dbName);
		}
	}

}
