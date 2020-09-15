package main.java.breakingPointTool.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.breakingPointTool.connection.DatabaseConnection;

public class DatabaseSaveData 
{
	public void saveMetricsInDatabase(String projectName, int versionNum, String className, String scope, double wmc, double dit, double cbo, double rfc, double lcom, 
			double wmc_dec, double nocc, double mpc, double dac, double size1, double size2, double dsc, double noh, double ana, double dam, double dcc, double camc, 
			double moa, double mfa, double nop, double cis, double nom, double Reusability, double Flexibility, double Understandability, double Functionality, 
			double Extendibility, double Effectiveness, double FanIn, double rem, double cpm) throws SQLException, NumberFormatException, IOException
	{
		// Save metrics that calculated from metrics calculator tool in database

		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "INSERT INTO javaMetrics (class_name,project_name,scope,wmc,dit,cbo,rfc,lcom,wmc_dec,nocc,mpc,dac,loc,number_of_properties,dsc,noh,ana,dam,dcc,camc,moa,mfa,nop,cis,nom," + 
				"reusability,flexibility,understandability,functionality,extendibility,effectiveness,fanIn,commit_hash,version,rem,cpm) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE class_name=VALUES(class_name)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, className);
			pstm.setString(2, projectName);
			pstm.setString(3, scope);
			pstm.setDouble(4, wmc);
			pstm.setDouble(5, dit);
			pstm.setDouble(6, cbo);
			pstm.setDouble(7, rfc);
			pstm.setDouble(8, lcom);
			pstm.setDouble(9, wmc_dec);
			pstm.setDouble(10, nocc);
			pstm.setDouble(11, mpc);
			pstm.setDouble(12, dac);
			pstm.setDouble(13, size1);
			pstm.setDouble(14, size2);
			pstm.setDouble(15, dsc);
			pstm.setDouble(16, noh);
			pstm.setDouble(17, ana);
			pstm.setDouble(18, dam);
			pstm.setDouble(19, dcc);
			pstm.setDouble(20, camc);
			pstm.setDouble(21, moa);
			pstm.setDouble(22, mfa);
			pstm.setDouble(23, nop);
			pstm.setDouble(24, cis);
			pstm.setDouble(25, nom);
			pstm.setDouble(26, Reusability);
			pstm.setDouble(27, Flexibility);
			pstm.setDouble(28, Understandability);
			pstm.setDouble(29, Functionality);		
			pstm.setDouble(30, Extendibility);
			pstm.setDouble(31, Effectiveness);
			pstm.setDouble(32, FanIn);
			pstm.setString(33, "empty");
			pstm.setString(34, String.valueOf(versionNum));
			pstm.setDouble(35, rem);
			pstm.setDouble(36, cpm);
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
		}	
	}

	public void saveBreakingPointInDatabase(String className, int versionNum, double breakingPoint, double principal, double interest, double k, double rate) throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "UPDATE javaMetrics SET principal = ? , interest = ? , breakingpoint = ? , frequency_of_change = ?, interest_probability = ?  WHERE class_name = ? AND version = ? ;";
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
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
			pstm.setInt(7, (int) codeSmells);
			pstm.setInt(8, (int) bugs);
			pstm.setInt(9, (int) vulnerabilities);
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
		}
	}

	public void saveTimestamp(String projectName, int versionNum) throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "INSERT INTO executionTimestap (project_name, version) VALUES (?,?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setInt(2, versionNum);
			pstm.executeUpdate();

		}  
		catch (SQLException ex) {
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
		}
	}

	public void deleteTimestamp(String projectName, int versionNum) throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;

		String query = "DELETE FROM executionTimestap WHERE project_name = ? and version = ?";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setInt(2, versionNum);
			pstm.executeUpdate();

		}  
		catch (SQLException ex) {
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
			/*if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}*/
		}
	}
}
