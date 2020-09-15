package main.java.breakingPointTool.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection 
{
	private static final String METRICS_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static String METRICS_URL = "";
	private static String METRICS_USERNAME = "";
	private static String METRICS_PASSWORD = "";

	// Connection Driver for Java and mySQL
	private static Connection connection = null;
	
	public DatabaseConnection(String user, String pass, String sonar)
	{
		DatabaseConnection.METRICS_USERNAME = user;
		DatabaseConnection.METRICS_PASSWORD = pass;
		DatabaseConnection.METRICS_URL = "jdbc:mysql://" + sonar + "?useSSL=false&autoReconnect=true";
	}

	public static Connection getConnection() 
	{
		if (connection != null) 
		{
			return connection;
		}
		return createConnection();
	}

	private static Connection createConnection() 
	{
		try {
			Class.forName(METRICS_DRIVER);
			connection = DriverManager.getConnection(METRICS_URL, METRICS_USERNAME, METRICS_PASSWORD);
			//connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}
}
