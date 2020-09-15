package main.java.breakingPointTool.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.breakingPointTool.connection.DatabaseConnection;

public class DatabaseSaveDataC 
{
	public void saveBreakingPointInDatabase(String className, int versionNum, double breakingPoint, double principal, double interest, double k, double rate) throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "UPDATE cMetrics SET principal = ? , interest = ? , breaking_point = ? , frequency_of_change = ?, interest_probability = ?  WHERE class_name = ? AND version = ? ;";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setDouble(1, principal);
			pstm.setDouble(2, interest);
			pstm.setDouble(3, breakingPoint);
			pstm.setFloat(4, (float) k);
			pstm.setFloat(5, (float) rate);
			pstm.setString(6, className);
			pstm.setDouble(7, versionNum);
			pstm.executeUpdate();

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database request failed. Please try again!");
		} 
		finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
		}
	}

	public void saveMetricsInDatabase(String projectName, int versionNum, String className, String scope, double loc, double cyclomatic_complexity, double wmoc, double loc_per_loc, double coupling, double cohesion) throws SQLException, NumberFormatException, IOException
	{
		// Save metrics that calculated from metrics calculator tool in database
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "INSERT INTO cMetrics (class_name,project_name,scope,loc, cyclomatic_complexity, number_of_functions, comments_density, version, coupling, cohesion) VALUES (?,?,?,?,?,?,?,?,?,?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, className);
			pstm.setString(2, projectName);
			pstm.setString(3, scope);
			pstm.setDouble(4, loc);
			pstm.setDouble(5, cyclomatic_complexity);
			pstm.setDouble(6, wmoc);
			pstm.setDouble(7, loc_per_loc);
			pstm.setDouble(8, versionNum);
			pstm.setDouble(9, coupling);
			pstm.setDouble(10, cohesion);
			pstm.executeUpdate();

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database request failed. Please try again!");
		} 
		finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
		}

	}

	public void savePrincipalMetrics(String className, String projectName, int version, double td, double principal,
			double codeSmells, double bugs, double vulnerabilities, double duplications, String type, double classes, 
			double complexity, double functions, double nloc, double statements, double comments_density, String language) throws SQLException, NumberFormatException, IOException
	{
		// Save metrics that calculated from metrics calculator tool in database
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "INSERT INTO principalMetrics (class_name, project_name, scope, version, td_minutes, principal, code_smells, bugs," + 
				"vulnerabilities, duplicated_lines_density, classes, complexity, functions, nloc, statements, comment_lines_density, language) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE class_name=VALUES(class_name)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, className);
			pstm.setString(2, projectName);
			pstm.setString(3, type);
			pstm.setDouble(4, version);
			pstm.setDouble(5, td);
			pstm.setDouble(6, principal);
			pstm.setDouble(7, codeSmells);
			pstm.setDouble(8, bugs);
			pstm.setDouble(9, vulnerabilities);
			pstm.setDouble(10, duplications);
			pstm.setDouble(11, classes);
			pstm.setDouble(12, complexity);
			pstm.setDouble(13, functions);
			pstm.setDouble(14, nloc);
			pstm.setDouble(15, statements);
			pstm.setDouble(16, comments_density);
			pstm.setString(17, language);
			pstm.executeUpdate();

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database request failed. Please try again!");
		} 
		finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
		}

	}

	public void updatePrincipal(String className, int versionNum, double principal) throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "UPDATE principalMetrics SET principal = ? WHERE class_name = ? AND version = ? ;";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setDouble(1, principal);
			pstm.setString(2, className);
			pstm.setDouble(3, versionNum);
			pstm.executeUpdate();

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database request failed. Please try again!");
		} 
		finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
		}
	}
}
