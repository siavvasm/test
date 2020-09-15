package main.java.breakingPointTool.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.breakingPointTool.artifact.ClassMetrics;
import main.java.breakingPointTool.artifact.FileMetricsC;
import main.java.breakingPointTool.artifact.PackageMetrics;
import main.java.breakingPointTool.artifact.PackageMetricsC;
import main.java.breakingPointTool.connection.DatabaseConnection;

// This class gets the analysis results and analyzes a new version instead of all software
public class GetAnalysisDataJava 
{
	private HashMap<String, ClassMetrics> classMetricsList = new HashMap<String, ClassMetrics>();
	private HashMap<String, PackageMetrics> packageMetricsList = new HashMap<String, PackageMetrics>();
	
	private HashMap<String, FileMetricsC> fileMetricsList = new HashMap<String, FileMetricsC>();
	private HashMap<String, PackageMetricsC> packageMetricsListC = new HashMap<String, PackageMetricsC>();
	
	public GetAnalysisDataJava()
	{
		this.classMetricsList = new HashMap<String, ClassMetrics>();
		this.packageMetricsList = new HashMap<String, PackageMetrics>();
		
		this.fileMetricsList = new HashMap<String, FileMetricsC>();
		this.packageMetricsListC = new HashMap<String, PackageMetricsC>();
	}

	public void getAnalysisDataBPTJava(String projectName, String scope, String version) 
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;
		ResultSet resultSet = null;
		String query;
		
		query = "SELECT * FROM javaMetrics WHERE project_name LIKE(?) and scope LIKE (?) and version LIKE (?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setString(2, scope);
			pstm.setString(3, version);
			resultSet = pstm.executeQuery();
			while (resultSet.next()) 
			{
				if (scope == "FIL")
				{
					ClassMetrics c = new ClassMetrics(projectName, resultSet.getString("class_name"));
					c.metricsfromMetricsCalculator(resultSet.getDouble("mpc"), resultSet.getDouble("nom"), resultSet.getDouble("dit"), 
							resultSet.getDouble("nocc"), resultSet.getDouble("rfc"), resultSet.getDouble("lcom"), resultSet.getDouble("wmc_dec"), 
							resultSet.getDouble("dac"), resultSet.getDouble("loc"), resultSet.getDouble("number_of_properties"));
					c.metricsfromChangeProneness(resultSet.getDouble("rem"), resultSet.getDouble("cpm"));
					c.setInterestProbability(resultSet.getDouble("interest_probability"));
					c.setAverageLOCChange(resultSet.getDouble("frequency_of_change"));
					classMetricsList.put(resultSet.getString("class_name"), c);	
				}
				else if (scope == "DIR")
				{
					PackageMetrics p = new PackageMetrics(projectName, resultSet.getString("class_name"));
					p.metricsfromMetricsCalculator(resultSet.getDouble("mpc"), resultSet.getDouble("nom"), resultSet.getDouble("dit"), 
							resultSet.getDouble("nocc"), resultSet.getDouble("rfc"), resultSet.getDouble("lcom"), resultSet.getDouble("wmc_dec"), 
							resultSet.getDouble("dac"), resultSet.getDouble("loc"), resultSet.getDouble("number_of_properties"));
					p.metricsfromChangeProneness(resultSet.getDouble("rem"), resultSet.getDouble("cpm"));
					p.setInterestProbability(resultSet.getDouble("interest_probability"));
					p.setAverageLocChange(resultSet.getDouble("frequency_of_change"));
					packageMetricsList.put(resultSet.getString("class_name"), p);	
				}
			}

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database select request failed. The project or the kee does not exist in the database."
					+ "Please try again!");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
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
	
	public void getAnalysisDataBPTC(String projectName, String scope, int version) 
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;
		ResultSet resultSet = null;
		String query;
		
		query = "SELECT * FROM cMetrics WHERE project_name LIKE (?) and scope LIKE (?) and version = (?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setString(2, scope);
			pstm.setDouble(3, version);
			resultSet = pstm.executeQuery();
			while (resultSet.next()) 
			{				
				if (scope == "FIL")
				{
					FileMetricsC c = fileMetricsList.get(resultSet.getString("class_name"));
					c.setAverageInterest(resultSet.getDouble("frequency_of_change"));
					fileMetricsList.replace(resultSet.getString("class_name"), c);	
				}
				else if (scope == "DIR")
				{
					PackageMetricsC c = packageMetricsListC.get(resultSet.getString("class_name"));
					c.setAverageInterest(resultSet.getDouble("frequency_of_change"));
					packageMetricsListC.replace(resultSet.getString("class_name"), c);		
				}
			}

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database select request failed. The project or the kee does not exist in the database."
					+ "Please try again!");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
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
	
	public void getAnalysisDataC(String projectName, String scope, int version) 
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;
		ResultSet resultSet = null;
		String query;
		
		query = "SELECT * FROM principalMetrics WHERE project_name LIKE(?) and scope LIKE (?) and version LIKE (?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setString(2, scope);
			pstm.setInt(3, version);
			resultSet = pstm.executeQuery();

			while (resultSet.next()) 
			{
				if (scope == "FIL")
				{
					FileMetricsC c = new FileMetricsC(projectName, resultSet.getString("class_name"));
					c.metricsfromSonar(resultSet.getDouble("classes"), resultSet.getDouble("complexity"), resultSet.getDouble("functions"), 
							resultSet.getDouble("nloc"), resultSet.getDouble("statements"), resultSet.getDouble("principal"), resultSet.getDouble("comment_lines_density"), resultSet.getDouble("code_smells"), 
									resultSet.getDouble("bugs"), resultSet.getDouble("vulnerabilities"), resultSet.getDouble("duplicated_lines_density"));
					fileMetricsList.put(resultSet.getString("class_name"), c);	
				}
				else if (scope == "DIR")
				{
					PackageMetricsC c = new PackageMetricsC(projectName, resultSet.getString("class_name"));
					c.metricsfromSonar(resultSet.getDouble("classes"), resultSet.getDouble("complexity"), resultSet.getDouble("functions"), 
							resultSet.getDouble("nloc"), resultSet.getDouble("statements"), resultSet.getDouble("principal"), resultSet.getDouble("comment_lines_density"), resultSet.getDouble("code_smells"), 
									resultSet.getDouble("bugs"), resultSet.getDouble("vulnerabilities"), resultSet.getDouble("duplicated_lines_density"));
					packageMetricsListC.put(resultSet.getString("class_name"), c);		
				}
			}


		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database select request failed. The project or the kee does not exist in the database."
					+ "Please try again!");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
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

	public void getAnalysisDataPrincipalSonar(String projectName, String scope, int version) 
	{
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement pstm = null;
		ResultSet resultSet = null;
		String query;
		
		query = "SELECT * FROM principalMetrics WHERE project_name LIKE(?) and scope LIKE (?) and version LIKE (?)";
		try 
		{
			pstm = conn.prepareStatement(query);
			pstm.setString(1, projectName);
			pstm.setString(2, scope);
			pstm.setInt(3, version);
			resultSet = pstm.executeQuery();
			while (resultSet.next()) 
			{
				if (scope == "FIL")
				{
					ClassMetrics c = classMetricsList.get(resultSet.getString("class_name"));
					c.metricsfromSonar(resultSet.getDouble("classes"), resultSet.getDouble("complexity"), resultSet.getDouble("functions"),
							resultSet.getDouble("nloc"), resultSet.getDouble("statements"), resultSet.getDouble("principal"), resultSet.getDouble("code_smells"), 
							resultSet.getDouble("bugs"), resultSet.getDouble("vulnerabilities"), resultSet.getDouble("duplicated_lines_density")) ;
					classMetricsList.replace(resultSet.getString("class_name"), c);	
				}
				else if (scope == "DIR")
				{
					PackageMetrics p = packageMetricsList.get(resultSet.getString("class_name"));
					p.metricsfromSonar(resultSet.getDouble("classes"), resultSet.getDouble("complexity"), resultSet.getDouble("functions"),
							resultSet.getDouble("nloc"), resultSet.getDouble("statements"), resultSet.getDouble("principal"), resultSet.getDouble("code_smells"), 
							resultSet.getDouble("bugs"), resultSet.getDouble("vulnerabilities"), resultSet.getDouble("duplicated_lines_density")) ;
					packageMetricsList.replace(resultSet.getString("class_name"), p);		
				}
			}

		} catch (SQLException ex) {
			Logger logger = Logger.getAnonymousLogger();
			logger.log(Level.SEVERE, "Exception was thrown: ", ex);
			System.out.println("Database select request failed. The project or the kee does not exist in the database."
					+ "Please try again!");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					Logger logger = Logger.getAnonymousLogger();
					logger.log(Level.SEVERE, "Exception was thrown: ", e);
				}
			}
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
	
	public ArrayList<ClassMetrics> getClassMetrics()
	{
		//Getting Collection of values       
        Collection<ClassMetrics> values = classMetricsList.values(); 
         
        //Creating an ArrayList of values  
        ArrayList<ClassMetrics> clMetrics = new ArrayList<ClassMetrics>(values);
        
        return clMetrics;
	}
	
	public HashMap<String, ClassMetrics> getClassMetricsMap()
	{        
        return classMetricsList;
	}
	
	public ArrayList<PackageMetrics> getPackageMetrics()
	{
		//Getting Collection of values       
        Collection<PackageMetrics> values = packageMetricsList.values(); 
         
        //Creating an ArrayList of values  
        ArrayList<PackageMetrics> pMetrics = new ArrayList<PackageMetrics>(values);
        
        return pMetrics;
	}
	
	public HashMap<String, PackageMetrics> getPackageMetricsMap()
	{        
        return packageMetricsList;
	}
	
	public ArrayList<FileMetricsC> getClassMetricsC()
	{
		//Getting Collection of values       
        Collection<FileMetricsC> values = fileMetricsList.values(); 
         
        //Creating an ArrayList of values  
        ArrayList<FileMetricsC> clMetrics = new ArrayList<FileMetricsC>(values);
        
        return clMetrics;
	}
	
	public HashMap<String, FileMetricsC> getClassMetricsCMap()
	{        
        return fileMetricsList;
	}
	
	public ArrayList<PackageMetricsC> getPackageMetricsC()
	{
		//Getting Collection of values       
        Collection<PackageMetricsC> values = packageMetricsListC.values(); 
         
        //Creating an ArrayList of values  
        ArrayList<PackageMetricsC> pMetrics = new ArrayList<PackageMetricsC>(values);
        
        return pMetrics;
	}
	
	public HashMap<String, PackageMetricsC> getPackageMetricsCMap()
	{        
        return packageMetricsListC;
	}
}
