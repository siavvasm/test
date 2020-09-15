package main.java.breakingPointTool.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SonarDatabaseConnection 
{
	private static final String SONAR_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String SONAR_URL = "";
	private static String SONAR_USERNAME = "";
	private static String SONAR_PASSWORD = "";

	// Connection Driver for Java and mySQL
	private static Connection connection = null;
	
	public SonarDatabaseConnection(String user, String pass, String sonar)
	{
		SonarDatabaseConnection.SONAR_USERNAME = user;
		SonarDatabaseConnection.SONAR_PASSWORD = pass;
		SonarDatabaseConnection.SONAR_URL = "jdbc:mysql://" + sonar + "?useSSL=false&autoReconnect=true";
	}

	public static Connection getConnection() throws InstantiationException, IllegalAccessException 
	{
		if (connection != null) 
		{
			return connection;
		}
		return createConnection();
	}

	private static Connection createConnection() throws InstantiationException, IllegalAccessException 
	{
		try {
	          Class.forName(SONAR_DRIVER);
			connection = DriverManager.getConnection(SONAR_URL, SONAR_USERNAME, SONAR_PASSWORD);
			//connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}
}
