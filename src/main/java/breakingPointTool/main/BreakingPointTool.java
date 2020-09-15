package main.java.breakingPointTool.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONException;
import org.xml.sax.SAXException;
import main.java.breakingPointTool.api.ApiCall;
import main.java.breakingPointTool.artifact.ProjectArtifact;
import main.java.breakingPointTool.calculations.AverageLocCalculation;
import main.java.breakingPointTool.calculations.AverageLocCalculationC;
import main.java.breakingPointTool.calculations.FindSimilarArtifacts;
import main.java.breakingPointTool.calculations.FindSimilarArtifactsC;
import main.java.breakingPointTool.calculations.OptimalArtifact;
import main.java.breakingPointTool.calculations.OptimalArtifactC;
import main.java.breakingPointTool.calculations.Results;
import main.java.breakingPointTool.calculations.ResultsC;
import main.java.breakingPointTool.connection.DatabaseConnection;
import main.java.breakingPointTool.connection.SonarDatabaseConnection;
import main.java.breakingPointTool.database.DatabaseGetData;
import main.java.breakingPointTool.database.DatabaseSaveData;
import main.java.breakingPointTool.database.GetAnalysisDataJava;
import main.java.breakingPointTool.externalTools.MetricsCalculator;
import main.java.breakingPointTool.externalTools.RippleEffectChangeProneness;
import main.java.breakingPointTool.versions.Versions;


/* This tool is called Breaking Point Tool and calculates quality metrics for Java, C++ and C
 * such as maintainability metrics and technical debt metrics such as principal, interest and breaking point.
 * 
 * It uses some other support tools.
 * 1) SonarQube (needs to be installed)
 * 2) Metrics Calculator for Java metrics (jar)
 * 3) Metrics Calculator for C (jar)
 * 4) Metrics Calculator for C++ (jar)
 * At the end, writes all the calculated metrics in a database.
 * 
 * The analysis occurs in class, package and project level. 
 * 
 * Prerequisites:
 * 1) A number of software versions
 * 2) A jar file for every software version for java code
 * 3) Specific structure of code - project -> package -> class (src/main/java) for java code
 * 4) Project name does not allow spaces
 * 5) For C path must be ""
 */

public class BreakingPointTool 
{
	private static Scanner keyboard;

	public static void main(String [ ] args) throws IOException, InterruptedException, NumberFormatException, SQLException, JSONException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException
	{	
		// Passing parameters through stream 
		String projectName = null, jarName = null, language = null;
		int versionsNum = 0;
		int typeAnalysis = 0;

		ArrayList <String> artifactLongNamesPackage = new ArrayList<>();

		ArrayList<FindSimilarArtifacts> similarCl = new ArrayList<FindSimilarArtifacts>();
		ArrayList<FindSimilarArtifacts> similarPck = new ArrayList<FindSimilarArtifacts>();

		ArrayList<FindSimilarArtifactsC> similarClC = new ArrayList<FindSimilarArtifactsC>();
		ArrayList<FindSimilarArtifactsC> similarPckC = new ArrayList<FindSimilarArtifactsC>();

		// NEW DATA STRUCTURES
		ProjectArtifact projectArtifacts = new ProjectArtifact();	

		if(args.length != 0)
		{		
			try {
				projectName = args[0];
				jarName = projectName;
				language = args[1];
				versionsNum = Integer.parseInt(args[2]);
				//path = args[3];
				typeAnalysis = Integer.parseInt(args[3]);
			}
			catch (ArrayIndexOutOfBoundsException e){
				System.out.println("Error: You should pass 6 parameters! 1) Project Name 2) JarName 3) Language 4) Num of Versions "
						+ "5) Path to the actuall java code");
			}
		}
		else
		{
			keyboard = new Scanner(System.in);
			System.out.println("Please type programming language for analysis: \nAvailable options:\n  1) Java \n"
					+ "  2) C 3) C++");
			language = keyboard.nextLine();
			System.out.println("Type the project Name: ");
			projectName = keyboard.nextLine();
			System.out.println("Type the number of versions: ");
			versionsNum = keyboard.nextInt();
			keyboard.nextLine(); // Consume newline left-over
			//System.out.println("Type the projects jar file name: "); 
			//jarName = keyboard.nextLine();	
			jarName = projectName;
			//System.out.println("Type path of main project for example: src/main/java/ "); 
			//path = keyboard.nextLine();	
			System.out.println("Type 1 for new analysis or 2 for a new version analysis to existed project"); 
			typeAnalysis = keyboard.nextInt();		
		}

		// NEW DATA STRUCTURES
		projectArtifacts.setprojectName(projectName);
		projectArtifacts.setNumOfVersions(versionsNum);

		// Configuration File, get all data from file and make the jar works in every machine
		String line;
		String exec = "" ;
		String projectPath = "";
		String jarPath = "";
		String serverUrl = "";
		// For sonarqube connection
		String usernameSQConnection = "", passwordSQConnection = "";
		// For sonarqube database
		String usernameSQ = "", passwordSQ = "";
		// For results database
		String usernameDBConnection = "", passwordDBConnection= "";
		String sonarName = "";
		String serverName = "";
		String dbName = "";

		java.net.URL jarLocationUrl = BreakingPointTool.class.getProtectionDomain().getCodeSource().getLocation();
		String jarLocation = new File(jarLocationUrl.toString()).getParent();
		jarLocation = jarLocation.replace("file:", "");

		// for eclipse execution
		int t = jarLocation.lastIndexOf("/");
		jarLocation = jarLocation.substring(0,t);

		String credentials = jarLocation + "/configurations.txt";

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
					usernameSQ = temp[1]; 
				}

				if (line.contains("passwordSQDB:")) 
				{  
					String[] temp = line.split("passwordSQDB:"); 
					passwordSQ = temp[1];
				} 

				// Metrics Database Credentials
				if (line.contains("username=")) 
				{ 
					String[] temp = line.split("username="); 
					usernameDBConnection = temp[1]; 
				}

				if (line.contains("password=")) 
				{  
					String[] temp = line.split("password="); 
					passwordDBConnection = temp[1];
				} 

				if (line.contains("sonarqube_execution:")) 
				{ 
					String[] temp = line.split("sonarqube_execution:"); 
					exec = temp[1]; 
				}

				if (line.contains("project_path:")) 
				{ 
					String[] temp = line.split("project_path:"); 
					projectPath = temp[1]; 
				}

				if (line.contains("jar_path:")) 
				{ 
					String[] temp = line.split("jar_path:");
					jarPath = temp[1]; 
				}

				if (line.contains("sonar_url:")) 
				{ 	
					String[] temp = line.split("sonar_url:");
					serverUrl = temp[1]; 
				}

				if (line.contains("sonarName:"))
				{
					String[] temp = line.split("sonarName:");
					sonarName = temp[1];
				}

				//  Metrics DB credentials
				if (line.contains("serverName="))
				{
					String[] temp = line.split("serverName=");
					serverName = temp[1];
				}

				if (line.contains("databaseName="))
				{
					String[] temp = line.split("databaseName=");
					dbName = temp[1];
				}


				// SonarQube Execution Credentials
				if (line.contains("usernameSQ:")) 
				{ 
					if (line.length() > 11) 
					{ 
						String[] temp = line.split("usernameSQ:"); 
						usernameSQConnection = temp[1]; 
					}
				}

				if (line.contains("passwordSQ:")) 
				{  
					if (line.length() > 11) 
					{ 
						String[] temp = line.split("passwordSQ:"); 
						passwordSQConnection = temp[1];
					}
				} 
			}
			br.close();
			// Set Credentials to Database
			new SonarDatabaseConnection(usernameSQ, passwordSQ, sonarName);
			new DatabaseConnection(usernameDBConnection, passwordDBConnection, serverName + "/" +  dbName);
			// Execute SonarQube
			//sonarQube(exec,projectName, versionsNum, projectPath, language, serverUrl, usernameSQConnection, passwordSQConnection, typeAnalysis); 
			// Create database tables
			createDatabaseTables();
		}


		if (language.equals("Java"))
		{
			// Databases call for every level		
			DatabaseGetData dbCall = new DatabaseGetData(projectName);	

			if (typeAnalysis == 1) {
				
				DatabaseSaveData dbSave = new DatabaseSaveData();
				for (int i = 0; i < versionsNum; i ++)
				{	
					dbSave.saveTimestamp(projectName, i);
				}
				
				for (int i = 0; i < versionsNum; i ++)
				{
					// Ripple Effect and Change Proneness Measure Execution
					RippleEffectChangeProneness rem = new RippleEffectChangeProneness();
					rem.ExtractJar(jarName, i, jarPath, projectName);

					// Metrics Calculator Execution
					MetricsCalculator metricsCalc = new MetricsCalculator();
					// sta  "" jarPath
					metricsCalc.executeOneVersion(jarName, i, jarPath, projectName);

					dbCall.getDirectoriesForProject(projectName, dbCall.getProjectsKees().get(i));
					dbCall.getClassesForProject(projectName, dbCall.getProjectsKees().get(i));
					Versions v = new Versions();
					v.setProjectName(projectName);
					v.setVersionId(i);			

					// Get metrics from Sonar API for every level
					ApiCall metricsFromSonarClassLevel = new ApiCall(serverUrl);
					metricsFromSonarClassLevel.getMetricsFromApiSonarClassLevel(dbCall.getClassesId(), language);
					metricsFromSonarClassLevel.getTDFromApiSonar(dbCall.getClassesId());
					metricsFromSonarClassLevel.getMetricCommentsDensityFromApi(dbCall.getClassesId());

					ApiCall metricsFromSonarPackageLevel = new ApiCall(serverUrl);
					metricsFromSonarPackageLevel.getMetricsFromSonarPackageLevel(dbCall.getPackagesId(), language);
					metricsFromSonarPackageLevel.getTDFromApiSonar(dbCall.getPackagesId());
					metricsFromSonarPackageLevel.getMetricCommentsDensityFromApi(dbCall.getPackagesId());

					/*
					 * for (String pc : dbCall.getPackagesId()) { if (pc.contains(path)) { String[]
					 * tempPackage = pc.split(path);
					 * 
					 * if (tempPackage.length == 1) continue;
					 * 
					 * artifactLongNamesPackage.add(tempPackage[1]); } }
					 */
					// Set metrics from metrics calculator at class and package level
					AverageLocCalculation calcAverageAllLevels = new AverageLocCalculation();
					calcAverageAllLevels.setMetricsToClassLevel(projectName, i);
					calcAverageAllLevels.setClassToPackageLevel(dbCall.getPackagesId(),projectName, i);	

					v.setPackageInProject(calcAverageAllLevels.getObjectsPackageMetrics());
					//artifactLongNamesPackage.clear();


					for (int j = 0; j < v.getPackages().size(); j++)
					{
						String pack1 = v.getPackages().get(j).getPackageName();

						for (int l = 0; l < metricsFromSonarPackageLevel.getArtifactNames().size(); l++)
						{
							String pack2 = metricsFromSonarPackageLevel.getArtifactNames().get(l);

							if (pack2.contains(pack1))
							{
								v.getPackages().get(j).metricsfromSonar(metricsFromSonarPackageLevel.getNumOfClasses().get(l),
										metricsFromSonarPackageLevel.getComplexity().get(l),
										metricsFromSonarPackageLevel.getFunctions().get(l),
										metricsFromSonarPackageLevel.getNcloc().get(l),
										metricsFromSonarPackageLevel.getStatements().get(l),
										metricsFromSonarPackageLevel.getTechnicalDebt().get(l),
										metricsFromSonarPackageLevel.getCodeSmells().get(l),
										metricsFromSonarPackageLevel.getBugs().get(l),
										metricsFromSonarPackageLevel.getVulnerabilities().get(l),
										metricsFromSonarPackageLevel.getDuplicationsDensity().get(l)
										);

								DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
								saveInDataBase.savePrincipalMetrics(pack1, projectName, i, 
										metricsFromSonarPackageLevel.getTechnicalDebt().get(l), 0, metricsFromSonarPackageLevel.getCodeSmells().get(l), 
										metricsFromSonarPackageLevel.getBugs().get(l), metricsFromSonarPackageLevel.getVulnerabilities().get(l), 
										metricsFromSonarPackageLevel.getDuplicationsDensity().get(l), "DIR", metricsFromSonarPackageLevel.getNumOfClasses().get(l),
										metricsFromSonarPackageLevel.getComplexity().get(l), metricsFromSonarPackageLevel.getFunctions().get(l),
										metricsFromSonarPackageLevel.getNcloc().get(l), metricsFromSonarPackageLevel.getStatements().get(l),
										0, language);

								break;
							}

						}

						// set sonar metrics in class level
						for (int k = 0; k < v.getPackages().get(j).getClassInProject().size(); k++)
						{
							String class1 = v.getPackages().get(j).getClassInProject().get(k).getClassName();

							for (int l = 0; l < metricsFromSonarClassLevel.getArtifactNames().size(); l++)
							{
								String class2 = metricsFromSonarClassLevel.getArtifactNames().get(l);

								if (class2.contains(class1))
								{
									v.getPackages().get(j).getClassInProject().get(k).metricsfromSonar(metricsFromSonarClassLevel.getNumOfClasses().get(l),
											metricsFromSonarClassLevel.getComplexity().get(l),
											metricsFromSonarClassLevel.getFunctions().get(l),
											metricsFromSonarClassLevel.getNcloc().get(l),
											metricsFromSonarClassLevel.getStatements().get(l),
											metricsFromSonarClassLevel.getTechnicalDebt().get(l),
											metricsFromSonarClassLevel.getCodeSmells().get(l),
											metricsFromSonarClassLevel.getBugs().get(l),
											metricsFromSonarClassLevel.getVulnerabilities().get(l),
											metricsFromSonarClassLevel.getDuplicationsDensity().get(l));

									DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
									saveInDataBase.savePrincipalMetrics(class1, projectName, i, 
											metricsFromSonarClassLevel.getTechnicalDebt().get(l), 0, metricsFromSonarClassLevel.getCodeSmells().get(l), 
											metricsFromSonarClassLevel.getBugs().get(l), metricsFromSonarClassLevel.getVulnerabilities().get(l), 
											metricsFromSonarClassLevel.getDuplicationsDensity().get(l), "FIL", metricsFromSonarClassLevel.getNumOfClasses().get(l),
											metricsFromSonarClassLevel.getComplexity().get(l), metricsFromSonarClassLevel.getFunctions().get(l),
											metricsFromSonarClassLevel.getNcloc().get(l), metricsFromSonarClassLevel.getStatements().get(l),
											0, language);

									break;
								}
							}
						}
					}

					projectArtifacts.setVersion(v);
					dbCall.clearData();
					

				} // End of FOR of versions

				// Calculate LOC 
				AverageLocCalculation calcAverageAllLevels = new AverageLocCalculation();
				calcAverageAllLevels.calculateLocPackageLevel(projectArtifacts);
				calcAverageAllLevels.calculateLocClassLevel(projectArtifacts);

				for (int i = 0; i < versionsNum; i++)
				{
					// Find similar classes
					FindSimilarArtifacts similarArtifacts = new FindSimilarArtifacts();
					similarCl = similarArtifacts.calculateSimilarityForClasses(projectArtifacts, i, typeAnalysis);

					// Find similar packages
					similarPck = similarArtifacts.calculateSimilarityForPackages(projectArtifacts, i, typeAnalysis);

					// Calculate Optimal Class
					OptimalArtifact optimalClass = new OptimalArtifact();
					optimalClass.calculateOptimalClass(similarCl, i);

					if (similarPckC.size() == 1)
					{
						Results rs = new Results();
						rs.calculateInterestOnePackage(similarPck.get(0).getPackage(), projectName, i);
					}
					else
					{
						OptimalArtifact optimalPackage = new OptimalArtifact();
						optimalPackage.calculateOptimalPackage(similarPck, i);
					} 
					
					dbSave.deleteTimestamp(projectName, i);
				}
			}
			else if (typeAnalysis == 2)
			{
				
				DatabaseSaveData dbSave = new DatabaseSaveData();
				dbSave.saveTimestamp(projectName, versionsNum - 1);
				
				// Get metrics from Database from previous version
				GetAnalysisDataJava classMetrics = new GetAnalysisDataJava();
				classMetrics.getAnalysisDataBPTJava(projectName, "FIL", Integer.toString(versionsNum - 2));
				classMetrics.getAnalysisDataPrincipalSonar(projectName, "FIL", versionsNum - 2);
				GetAnalysisDataJava packageMetrics = new GetAnalysisDataJava();
				packageMetrics.getAnalysisDataBPTJava(projectName, "DIR", Integer.toString(versionsNum - 2));
				packageMetrics.getAnalysisDataPrincipalSonar(projectName, "DIR", versionsNum - 2);

				//################################ Analysis of NEW VERSION ###########################################
				// Ripple Effect and Change Proneness Measure Execution
				RippleEffectChangeProneness rem = new RippleEffectChangeProneness();
				rem.ExtractJar(jarName, versionsNum-1, jarPath, projectName);

				// Metrics Calculator Execution
				MetricsCalculator metricsCalc = new MetricsCalculator();
				// sta  "" jarPath
				metricsCalc.executeOneVersion(jarName, versionsNum-1, jarPath, projectName);

				// Get directories and files names from SonarQube DB Last Version
				dbCall.getDirectoriesForProject(projectName, dbCall.getProjectsKees().get(versionsNum - 1));
				dbCall.getClassesForProject(projectName, dbCall.getProjectsKees().get(versionsNum - 1));			

				// Get metrics from Sonar API for every level - NEW VERSION
				ApiCall metricsFromSonarClassLevel = new ApiCall(serverUrl);
				metricsFromSonarClassLevel.getMetricsFromApiSonarClassLevel(dbCall.getClassesId(), language);
				metricsFromSonarClassLevel.getTDFromApiSonar(dbCall.getClassesId());
				metricsFromSonarClassLevel.getMetricCommentsDensityFromApi(dbCall.getClassesId());

				ApiCall metricsFromSonarPackageLevel = new ApiCall(serverUrl);
				metricsFromSonarPackageLevel.getMetricsFromSonarPackageLevel(dbCall.getPackagesId(), language);
				metricsFromSonarPackageLevel.getTDFromApiSonar(dbCall.getPackagesId());
				metricsFromSonarPackageLevel.getMetricCommentsDensityFromApi(dbCall.getPackagesId());

				/*
				 * for (String pc : dbCall.getPackagesId()) { if (pc.contains(path)) { String[]
				 * tempPackage = pc.split(path);
				 * 
				 * if (tempPackage.length == 1) continue;
				 * 
				 * artifactLongNamesPackage.add(tempPackage[1]); }
				 * 
				 * }
				 */

				// Set metrics from metrics calculator at class and package level
				AverageLocCalculation calcAverageAllLevels = new AverageLocCalculation();
				// Save Metrics Calculator Class Metrics
				calcAverageAllLevels.setMetricsToClassLevel(projectName, versionsNum-1);
				// Save Metrics Calculator Package Metrics
				calcAverageAllLevels.setClassToPackageLevel(dbCall.getPackagesId(),projectName, versionsNum-1);

				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! maybe DELETE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				Versions v = new Versions();
				v.setProjectName(projectName);
				v.setVersionId(versionsNum-1);


				v.setPackageInProject(calcAverageAllLevels.getObjectsPackageMetrics());
				//artifactLongNamesPackage.clear();

				for (int j = 0; j < v.getPackages().size(); j++)
				{
					String pack1 = v.getPackages().get(j).getPackageName();

					for (int l = 0; l < metricsFromSonarPackageLevel.getArtifactNames().size(); l++)
					{
						String pack2 = metricsFromSonarPackageLevel.getArtifactNames().get(l);

						if (pack2.contains(pack1))
						{
							v.getPackages().get(j).metricsfromSonar(metricsFromSonarPackageLevel.getNumOfClasses().get(l),
									metricsFromSonarPackageLevel.getComplexity().get(l),
									metricsFromSonarPackageLevel.getFunctions().get(l),
									metricsFromSonarPackageLevel.getNcloc().get(l),
									metricsFromSonarPackageLevel.getStatements().get(l),
									metricsFromSonarPackageLevel.getTechnicalDebt().get(l),
									metricsFromSonarPackageLevel.getCodeSmells().get(l),
									metricsFromSonarPackageLevel.getBugs().get(l),
									metricsFromSonarPackageLevel.getVulnerabilities().get(l),
									metricsFromSonarPackageLevel.getDuplicationsDensity().get(l)
									);

							DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
							saveInDataBase.savePrincipalMetrics(pack1, projectName, versionsNum-1, 
									metricsFromSonarPackageLevel.getTechnicalDebt().get(l), 0, metricsFromSonarPackageLevel.getCodeSmells().get(l), 
									metricsFromSonarPackageLevel.getBugs().get(l), metricsFromSonarPackageLevel.getVulnerabilities().get(l), 
									metricsFromSonarPackageLevel.getDuplicationsDensity().get(l), "DIR", metricsFromSonarPackageLevel.getNumOfClasses().get(l),
									metricsFromSonarPackageLevel.getComplexity().get(l), metricsFromSonarPackageLevel.getFunctions().get(l),
									metricsFromSonarPackageLevel.getNcloc().get(l), metricsFromSonarPackageLevel.getStatements().get(l),
									0, language);

							break;
						}

					}

					// set sonar metrics in class level
					for (int k = 0; k < v.getPackages().get(j).getClassInProject().size(); k++)
					{
						String class1 = v.getPackages().get(j).getClassInProject().get(k).getClassName();

						for (int l = 0; l < metricsFromSonarClassLevel.getArtifactNames().size(); l++)
						{
							String class2 = metricsFromSonarClassLevel.getArtifactNames().get(l);

							if (class2.contains(class1))
							{
								v.getPackages().get(j).getClassInProject().get(k).metricsfromSonar(metricsFromSonarClassLevel.getNumOfClasses().get(l),
										metricsFromSonarClassLevel.getComplexity().get(l),
										metricsFromSonarClassLevel.getFunctions().get(l),
										metricsFromSonarClassLevel.getNcloc().get(l),
										metricsFromSonarClassLevel.getStatements().get(l),
										metricsFromSonarClassLevel.getTechnicalDebt().get(l),
										metricsFromSonarClassLevel.getCodeSmells().get(l),
										metricsFromSonarClassLevel.getBugs().get(l),
										metricsFromSonarClassLevel.getVulnerabilities().get(l),
										metricsFromSonarClassLevel.getDuplicationsDensity().get(l));

								DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
								saveInDataBase.savePrincipalMetrics(class1, projectName, versionsNum - 1, 
										metricsFromSonarClassLevel.getTechnicalDebt().get(l), 0, metricsFromSonarClassLevel.getCodeSmells().get(l), 
										metricsFromSonarClassLevel.getBugs().get(l), metricsFromSonarClassLevel.getVulnerabilities().get(l), 
										metricsFromSonarClassLevel.getDuplicationsDensity().get(l), "FIL", metricsFromSonarClassLevel.getNumOfClasses().get(l),
										metricsFromSonarClassLevel.getComplexity().get(l), metricsFromSonarClassLevel.getFunctions().get(l),
										metricsFromSonarClassLevel.getNcloc().get(l), metricsFromSonarClassLevel.getStatements().get(l),
										0, language);

								break;
							}
						}
					}
				}

				projectArtifacts.setVersion(v);
				dbCall.clearData();

				// Calculate LOC 
				AverageLocCalculation calcLOC = new AverageLocCalculation();

				calcLOC.calculateLOCClassLevelNewVersion(classMetrics.getClassMetricsMap(), v.getPackages(), versionsNum -1);
				calcLOC.calculateLOCCPackageLevelNewVersion(packageMetrics.getPackageMetricsMap(), v.getPackages(), versionsNum -1);


				// Find similar classes
				FindSimilarArtifacts similarArtifacts = new FindSimilarArtifacts();
				similarCl = similarArtifacts.calculateSimilarityForClasses(projectArtifacts, versionsNum-1, typeAnalysis);

				// Find similar packages
				similarPck = similarArtifacts.calculateSimilarityForPackages(projectArtifacts, versionsNum-1, typeAnalysis);

				// Calculate Optimal Class
				OptimalArtifact optimalClass = new OptimalArtifact();
				optimalClass.calculateOptimalClass(similarCl, versionsNum-1);

				//OptimalArtifact optimalPackage = new OptimalArtifact();
				//optimalPackage.calculateOptimalPackage(similarPck, versionsNum-1);
				
				if (similarPckC.size() == 1)
				{
					Results rs = new Results();
					rs.calculateInterestOnePackage(similarPck.get(0).getPackage(), projectName, versionsNum - 1);
				}
				else
				{
					OptimalArtifact optimalPackage = new OptimalArtifact();
					optimalPackage.calculateOptimalPackage(similarPck, versionsNum-1);
				} 

				dbSave.deleteTimestamp(projectName, versionsNum - 1);

			}


			// CHECK DATA PRINT
			/*
			for (int i = 0; i < versionsNum; i++)
			{
				System.out.println("Project ID: " + projectArtifacts.getProjectName());
				System.out.println("Version ID: " + projectArtifacts.getVersions().get(i).getVersionId());
				for (int j = 0; j < projectArtifacts.getVersions().get(i).getPackages().size(); j++)
				{
					System.out.println("Package: " +  projectArtifacts.getVersions().get(i).getPackages().get(j).getPackageName());
					System.out.println("Size1: " +  projectArtifacts.getVersions().get(i).getPackages().get(j).getSize1());
					System.out.println("TD: " +  projectArtifacts.getVersions().get(i).getPackages().get(j).getTD());
					System.out.println("LOC: " +  projectArtifacts.getVersions().get(i).getPackages().get(j).getAverageLocChange());


					for (int x = 0; x < projectArtifacts.getVersions().get(i).getPackages().get(j).getClassInProject().size(); x++)
					{
						System.out.println("Class : " + projectArtifacts.getVersions().get(i).getPackages().get(j).getClassInProject().get(x).getClassName());
						System.out.println("Size1 : " + projectArtifacts.getVersions().get(i).getPackages().get(j).getClassInProject().get(x).getSize1());
						System.out.println("TD : " + projectArtifacts.getVersions().get(i).getPackages().get(j).getClassInProject().get(x).getTD());
						System.out.println("LOC : " + projectArtifacts.getVersions().get(i).getPackages().get(j).getClassInProject().get(x).getAverageLocChange());
					}
				}
				System.out.println("---------");
			}*/

		}
		else if (language.equals("C") || language.equals("C++"))
		{
			// Databases call for every level		
			DatabaseGetData dbCall = new DatabaseGetData(projectName);	
			if (typeAnalysis == 1) 
			{
				DatabaseSaveData dbSave = new DatabaseSaveData();
				for (int i = 0; i < versionsNum; i ++)
				{	
					dbSave.saveTimestamp(projectName, i);
				}
				
				for (int i = 0; i < versionsNum; i ++)
				{
					String p = projectPath + File.separator + projectName + File.separator + projectName + i;

					dbCall.getDirectoriesForProject(projectName, dbCall.getProjectsKees().get(i));
					dbCall.getClassesForProject(projectName, dbCall.getProjectsKees().get(i));
					Versions v = new Versions();
					v.setProjectName(projectName);
					v.setVersionId(i);	

					// Get metrics from Sonar API for every level
					ApiCall metricsFromSonarClassLevel = new ApiCall(serverUrl);

					metricsFromSonarClassLevel.getMetricsFromApiSonarClassLevel(dbCall.getClassesId(), language);
					metricsFromSonarClassLevel.getTDFromApiSonar(dbCall.getClassesId());
					metricsFromSonarClassLevel.getMetricCommentsDensityFromApi(dbCall.getClassesId());

					ApiCall metricsFromSonarPackageLevel = new ApiCall(serverUrl);
					metricsFromSonarPackageLevel.getMetricsFromSonarPackageLevel(dbCall.getPackagesId(), language);
					metricsFromSonarPackageLevel.getTDFromApiSonar(dbCall.getPackagesId());
					metricsFromSonarPackageLevel.getMetricCommentsDensityFromApi(dbCall.getPackagesId());

					for (String pc : dbCall.getPackagesId())
					{
						if (pc.contains("test"))
							continue;
						String[] tempPackage = pc.split(":");

						if (tempPackage.length == 1)
							continue;
						//String[] temp1 = tempPackage[1].split(".java");
						//artifactLongNamesPackage.add("com/" + temp1[0]); 
						//System.out.println("check after: " +tempPackage[1]);
						artifactLongNamesPackage.add(tempPackage[1]); 
					}

					// Set metrics from sonar at class and package level	
					AverageLocCalculationC calcAverageAllLevels = new AverageLocCalculationC();
					calcAverageAllLevels.setMetricsToClassLevel(metricsFromSonarClassLevel, projectName, i, language, p, credentials);
					calcAverageAllLevels.setClassToPackageLevel(artifactLongNamesPackage,projectName, i, metricsFromSonarPackageLevel, language);

					v.setPackageInProjectC(calcAverageAllLevels.getObjectsPackageMetrics());
					artifactLongNamesPackage.clear();

					projectArtifacts.setVersion(v);
					dbCall.clearData();
					

				} 
				
				// Calculate LOC
				AverageLocCalculationC calcAverageAllLevels = new AverageLocCalculationC();
				calcAverageAllLevels.calculateLocPackageLevel(projectArtifacts);
				calcAverageAllLevels.calculateLocClassLevel(projectArtifacts);

				for (int i = 0; i < versionsNum; i++)
				{
					System.out.println("-------------------------");
					// Find similar classes
					FindSimilarArtifactsC similarArtifacts = new FindSimilarArtifactsC();
					similarClC = similarArtifacts.calculateSimilarityForClasses(projectArtifacts, i, typeAnalysis);

					// Find similar packages
					similarPckC = similarArtifacts.calculateSimilarityForPackages(projectArtifacts, i, typeAnalysis);

					// Calculate Optimal Class
					OptimalArtifactC optimalClass = new OptimalArtifactC();
					optimalClass.calculateOptimalClass(similarClC, i);

					// If only one package
					if (similarPckC.size() == 1)
					{
						ResultsC rs = new ResultsC();
						rs.calculateInterestOnePackage(similarPckC.get(0).getPackage(), projectName, i);
					}
					else
					{
						OptimalArtifactC optimalPackage = new OptimalArtifactC();
						optimalPackage.calculateOptimalPackage(similarPckC, i); 
					} 
					
					dbSave.deleteTimestamp(projectName, i);
				}
			}

			else if (typeAnalysis == 2)
			{
				
				DatabaseSaveData dbSave = new DatabaseSaveData();
				dbSave.saveTimestamp(projectName, versionsNum - 1);

				String p = projectPath + File.separator + projectName + File.separator + projectName + (versionsNum - 1);

				// Get metrics from Database from previous version
				GetAnalysisDataJava classMetrics = new GetAnalysisDataJava();
				classMetrics.getAnalysisDataC(projectName, "FIL", versionsNum - 2);
				classMetrics.getAnalysisDataBPTC(projectName, "FIL", versionsNum - 2);

				GetAnalysisDataJava packageMetrics = new GetAnalysisDataJava();				
				packageMetrics.getAnalysisDataC(projectName, "DIR", versionsNum - 2);
				packageMetrics.getAnalysisDataBPTC(projectName, "DIR", versionsNum - 2);

				dbCall.getDirectoriesForProject(projectName, dbCall.getProjectsKees().get(versionsNum - 1));
				dbCall.getClassesForProject(projectName, dbCall.getProjectsKees().get(versionsNum - 1));

				Versions v = new Versions();
				v.setProjectName(projectName);
				v.setVersionId(versionsNum-1);	

				// Get metrics from Sonar API for every level
				ApiCall metricsFromSonarClassLevel = new ApiCall(serverUrl);

				metricsFromSonarClassLevel.getMetricsFromApiSonarClassLevel(dbCall.getClassesId(), language);
				metricsFromSonarClassLevel.getTDFromApiSonar(dbCall.getClassesId());
				metricsFromSonarClassLevel.getMetricCommentsDensityFromApi(dbCall.getClassesId());

				ApiCall metricsFromSonarPackageLevel = new ApiCall(serverUrl);
				metricsFromSonarPackageLevel.getMetricsFromSonarPackageLevel(dbCall.getPackagesId(), language);
				metricsFromSonarPackageLevel.getTDFromApiSonar(dbCall.getPackagesId());
				metricsFromSonarPackageLevel.getMetricCommentsDensityFromApi(dbCall.getPackagesId());

				for (String pc : dbCall.getPackagesId())
				{
					//System.out.println("check before: " + pc);
					if (pc.contains("test"))
						continue;
					if (pc.contains("res"))
						continue;
					if (pc.contains("tools"))
						continue;
					//String[] tempPackage = pc.split("src/main/java/");
					String[] tempPackage = pc.split(":");
					if (tempPackage.length == 1)
						continue;
					//String[] temp1 = tempPackage[1].split(".java");
					//artifactLongNamesPackage.add("com/" + temp1[0]); 
					//System.out.println("check after: " +tempPackage[1]);
					artifactLongNamesPackage.add(tempPackage[1]); 
				}
				// Set metrics from sonar at class and package level	
				AverageLocCalculationC calcAverageAllLevels = new AverageLocCalculationC();
				calcAverageAllLevels.setMetricsToClassLevel(metricsFromSonarClassLevel, projectName, versionsNum-1, language, p, credentials);
				calcAverageAllLevels.setClassToPackageLevel(artifactLongNamesPackage,projectName, versionsNum-1, metricsFromSonarPackageLevel, language);

				v.setPackageInProjectC(calcAverageAllLevels.getObjectsPackageMetrics());
				artifactLongNamesPackage.clear();

				projectArtifacts.setVersion(v);
				dbCall.clearData();

				// Calculate LOC

				AverageLocCalculationC calcLOC = new AverageLocCalculationC();
				calcLOC.calculateLOCClassLevelNewVersion(classMetrics.getClassMetricsCMap(), v.getPackagesC(), versionsNum -1);
				calcLOC.calculateLOCPackageLevelNewVersion(packageMetrics.getPackageMetricsCMap(), v.getPackagesC(), versionsNum -1);

				// Find similar classes
				FindSimilarArtifactsC similarArtifacts = new FindSimilarArtifactsC();
				similarClC = similarArtifacts.calculateSimilarityForClasses(projectArtifacts, versionsNum-1, typeAnalysis);

				// Find similar packages
				similarPckC = similarArtifacts.calculateSimilarityForPackages(projectArtifacts, versionsNum-1, typeAnalysis);

				// Calculate Optimal Class
				OptimalArtifactC optimalClass = new OptimalArtifactC();
				optimalClass.calculateOptimalClass(similarClC, versionsNum-1);
				
				if (similarPckC.size() == 1)
				{
					ResultsC rs = new ResultsC();
					rs.calculateInterestOnePackage(similarPckC.get(0).getPackage(), projectName, versionsNum-1);
				}
				else
				{
					OptimalArtifactC optimalPackage = new OptimalArtifactC();
					optimalPackage.calculateOptimalPackage(similarPckC, versionsNum-1); 
				}

				dbSave.deleteTimestamp(projectName, versionsNum - 1);

			}


			/*
			// CHECK DATA PRINT
			for (int i = 0; i < versionsNum; i++)
			{
				System.out.println("Project ID: " + projectArtifacts.getProjectName());
				System.out.println("Version ID: " + projectArtifacts.getVersions().get(i).getVersionId());
				for (int j = 0; j < projectArtifacts.getVersions().get(i).getPackagesC().size(); j++)
				{
					System.out.println("Package: " +  projectArtifacts.getVersions().get(i).getPackagesC().get(j).getPackageName());
					System.out.println("Size1: " +  projectArtifacts.getVersions().get(i).getPackagesC().get(j).getNcloc());
					System.out.println("TD: " +  projectArtifacts.getVersions().get(i).getPackagesC().get(j).getTD());
					System.out.println("Commends Density: " +  projectArtifacts.getVersions().get(i).getPackagesC().get(j).getCommentsDensity());


					for (int x = 0; x < projectArtifacts.getVersions().get(i).getPackagesC().get(j).getClassInProject().size(); x++)
					{
						System.out.println("Class : " + projectArtifacts.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(x).getClassName());
						System.out.println("Size1 : " + projectArtifacts.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(x).getNcloc());
						System.out.println("TD : " + projectArtifacts.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(x).getTD());
						System.out.println("Commends Density : " + projectArtifacts.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(x).getCommentsDensity());
					}
				}
				System.out.println("---------");
			}*/



		}
		else
		{
			System.out.println("Programming language does not supported. Execute the software again and choose one of the available options.");
		}

		deleteFile(versionsNum);
	}

	public static void sonarQube (String exec, String projectName, int versions, String projectPath, String language, String serverUrl, String username, String password, int typeAnalysis) throws FileNotFoundException, InterruptedException
	{
		String fileName = "sonar-project.properties";
		//LocalDate localDate = LocalDate.now();
		for (int i = 0; i < versions; i++) 
		{
			if (typeAnalysis == 2)
			{
				i = versions - 1;
				versions = 1;
			}
			// Create sonar-properties file
			File newFile = new File(projectPath + File.separator + projectName + File.separator + projectName+ i + File.separator + fileName);
			System.out.println(newFile);
			String key = projectName + i;

			System.out.println("Execute version: " + i);
			PrintWriter printWriter = new PrintWriter (newFile);
			printWriter.println ("# Required metadata");
			printWriter.println ("sonar.projectKey=" + key); 
			printWriter.println ("sonar.projectName=" + projectName);
			printWriter.println ("sonar.projectVersion=" + i);		    
			//printWriter.println ("sonar.projectDate=" + DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate));
			printWriter.println ();

			printWriter.println ("# Comma-separated paths to directories with sources (required)");
			printWriter.println ("sonar.sources=.");
			printWriter.println ();

			printWriter.println ("# Language");
			printWriter.println ("sonar.language=" + language.toLowerCase());
			if (!language.equals("Java"))
				printWriter.println("sonar.cxx.suffixes.sources=cpp,c,cxx,h,hxx,hpp");
			printWriter.println ();

			printWriter.println ("# Encoding of the source files");		    
			printWriter.println ("sonar.sourceEncoding=UTF-8");
			printWriter.println ("sonar.java.binaries=.");
			//printWriter.println ("sonar.projectBaseDir=" + projectPath + File.separator + projectName + File.separator + projectName+ i + File.separator);

			// Default : http://localhost:9000
			printWriter.println ("# Server Configuration");
			printWriter.println ("sonar.host.url=" + serverUrl); 
			printWriter.println ("sonar.login=" + username);
			// Password is blank if token is used
			printWriter.println ("sonar.password=" + password);
			printWriter.close ();

			// Execute SonarQube
			try { 
				int ch; 
				System.out.println("inside try");
				ProcessBuilder pb = new ProcessBuilder(exec); 
				pb.directory(new File(projectPath + File.separator + projectName + File.separator + projectName + i + File.separator)); 
				System.out.println("File: " + projectPath + File.separator + projectName + File.separator + projectName + i + File.separator);
				Process shellProcess = pb.start();  
				shellProcess.waitFor(); 

				InputStreamReader myIStreamReader = new 
						InputStreamReader(shellProcess.getInputStream()); 

				while ((ch = myIStreamReader.read()) != -1) { 
					System.out.print((char)ch); 
				} 
			} catch (IOException anIOException) { 
				System.out.println(anIOException); 
			} catch(Exception e) { 
				e.printStackTrace(); 

			} 

			System.out.println("SonarQube Execution Done");

		}
	}

	// Delete all files from external tools
	public static void deleteFile(int versions)
	{	
		String file;
		for (int i = 0; i < versions; i++)
		{
			file = "output" + i + ".csv";
			delete(file);
		}

		delete("output.csv");
		delete("metrics.txt");
		delete("rem_and_cpm_metrics_classLevel.csv");
		delete("rem_and_cpm_metrics_packageLevel.csv");
	}

	public static void delete(String file)
	{
		try
		{ 
			Files.deleteIfExists(Paths.get(file)); 
		} 
		catch(NoSuchFileException e) 
		{ 
			System.out.println("No such file/directory exists"); 
		} 
		catch(DirectoryNotEmptyException e) 
		{ 
			System.out.println("Directory is not empty."); 
		} 
		catch(IOException e) 
		{ 
			System.out.println("Invalid permissions."); 
		} 

		System.out.println("Deletion successful."); 
	}

	// Check if tables are created, if not, create them
	public static void createDatabaseTables() throws SQLException
	{
		Connection conn = DatabaseConnection.getConnection();
		DatabaseMetaData dbm = (DatabaseMetaData) conn.getMetaData();
		// check if "employee" table is there
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
