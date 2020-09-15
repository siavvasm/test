package main.java.breakingPointTool.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import main.java.breakingPointTool.connection.DatabaseConnection;

// Check if tables are created, if not, create them. You should have the database
public class TablesCreation 
{
	// Check if tables are created, if not, create them
	public void createDatabaseTables() throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		DatabaseMetaData dbm = (DatabaseMetaData) conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "principalMetrics", null);
		
		if (tables.next()) 
		{
			System.out.println("Table principalMetrics exists!");
		}
		else 
		{
			Statement stmt = null;
			try {
				stmt = (Statement) conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String query = "CREATE TABLE `principalMetrics` (\n" + 
					"  `class_name` varchar(500) NOT NULL,\n" + 
					"  `project_name` varchar(500) NOT NULL,\n" + 
					"  `version` int(11) NOT NULL,\n" + 
					"  `td_minutes` float(100,0) NOT NULL,\n" + 
					"  `principal` double(100,0) NOT NULL,\n" + 
					"  `code_smells` int(100) NOT NULL,\n" + 
					"  `bugs` int(100) NOT NULL,\n" + 
					"  `vulnerabilities` int(100) NOT NULL,\n" + 
					"  `duplicated_lines_density` float(100,0) NOT NULL,\n" + 
					"  `scope` varchar(100) NOT NULL,\n" + 
					"  `classes` double(100,0) DEFAULT NULL,\n" + 
					"  `complexity` double(100,0) DEFAULT NULL,\n" + 
					"  `functions` double DEFAULT NULL,\n" + 
					"  `nloc` double DEFAULT NULL,\n" + 
					"  `statements` double DEFAULT NULL,\n" + 
					"  `comment_lines_density` double(100,0) DEFAULT NULL,\n" + 
					"  `language` varchar(500) NOT NULL\n" + 
					") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

			stmt.executeUpdate(query);

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		tables = null;
		tables = dbm.getTables(null, null, "cMetrics", null);
		if (tables.next()) 
		{
			System.out.println("Table cMetrics exists!");
		}
		else 
		{
			Statement stmt = null;
			try {
				stmt = (Statement) conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String query = "CREATE TABLE `cMetrics` (\n" + 
					"  `id` int(10) NOT NULL,\n" + 
					"  `class_name` varchar(500) NOT NULL,\n" + 
					"  `project_name` varchar(100) NOT NULL,\n" + 
					"  `scope` varchar(45) NOT NULL,\n" + 
					"  `loc` double NOT NULL,\n" + 
					"  `cyclomatic_complexity` double NOT NULL,\n" + 
					"  `number_of_functions` double NOT NULL,\n" + 
					"  `comments_density` double NOT NULL,\n" + 
					"  `version` int(45) NOT NULL,\n" + 
					"  `principal` double DEFAULT '0',\n" + 
					"  `interest` double DEFAULT '0',\n" + 
					"  `breaking_point` double DEFAULT '0',\n" + 
					"  `frequency_of_change` double DEFAULT '0',\n" + 
					"  `interest_probability` double DEFAULT '0',\n" + 
					"  `coupling` double DEFAULT '0',\n" + 
					"  `cohesion` double DEFAULT '0'\n" + 
					") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

			stmt.executeUpdate(query);

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		tables = null;
		tables = dbm.getTables(null, null, "javaMetrics", null);
		if (tables.next()) 
		{
			System.out.println("Table javaMetrics exists!");
		}
		else 
		{
			Statement stmt = null;
			try {
				stmt = (Statement) conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String query = "CREATE TABLE `javaMetrics` (\n" + 
					"  `id` int(10) NOT NULL,\n" + 
					"  `class_name` varchar(100) NOT NULL,\n" + 
					"  `project_name` varchar(100) NOT NULL,\n" + 
					"  `scope` varchar(45) DEFAULT NULL,\n" + 
					"  `wmc` double(40,0) NOT NULL,\n" + 
					"  `dit` double(40,0) NOT NULL,\n" + 
					"  `cbo` double(40,0) NOT NULL,\n" + 
					"  `rfc` double(40,0) NOT NULL,\n" + 
					"  `lcom` double(40,0) NOT NULL,\n" + 
					"  `wmc_dec` double(40,0) NOT NULL,\n" + 
					"  `nocc` double(40,0) NOT NULL,\n" + 
					"  `mpc` double(40,0) NOT NULL,\n" + 
					"  `dac` double(40,0) NOT NULL,\n" + 
					"  `loc` double(40,0) NOT NULL,\n" + 
					"  `number_of_properties` double(40,0) NOT NULL,\n" + 
					"  `dsc` double(40,0) NOT NULL,\n" + 
					"  `noh` double(40,0) NOT NULL,\n" + 
					"  `ana` double(40,0) NOT NULL,\n" + 
					"  `dam` double(40,0) NOT NULL,\n" + 
					"  `dcc` double(40,0) NOT NULL,\n" + 
					"  `camc` double(40,0) NOT NULL,\n" + 
					"  `moa` double(40,0) NOT NULL,\n" + 
					"  `mfa` double(40,0) NOT NULL,\n" + 
					"  `nop` double(40,0) NOT NULL,\n" + 
					"  `cis` double(40,0) NOT NULL,\n" + 
					"  `nom` double(40,0) NOT NULL,\n" + 
					"  `reusability` double(40,0) NOT NULL,\n" + 
					"  `flexibility` double(40,0) NOT NULL,\n" + 
					"  `understandability` double(40,0) NOT NULL,\n" + 
					"  `functionality` double(40,0) NOT NULL,\n" + 
					"  `extendibility` double(40,0) NOT NULL,\n" + 
					"  `effectiveness` double(40,0) NOT NULL,\n" + 
					"  `fanIn` double(40,0) NOT NULL,\n" + 
					"  `commit_hash` varchar(100) NOT NULL,\n" + 
					"  `version` int(100) NOT NULL,\n" + 
					"  `principal` double DEFAULT '0',\n" + 
					"  `interest` double DEFAULT '0',\n" + 
					"  `breakingpoint` double DEFAULT '0',\n" + 
					"  `frequency_of_change` double DEFAULT '0',\n" + 
					"  `interest_probability` double DEFAULT '0',\n" + 
					"  `rem` double DEFAULT '0',\n" + 
					"  `cpm` double DEFAULT '0'\n" + 
					") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

			stmt.executeUpdate(query);

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
