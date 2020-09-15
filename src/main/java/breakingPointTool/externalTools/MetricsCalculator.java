package main.java.breakingPointTool.externalTools;

import java.io.File;
import java.io.IOException;

public class MetricsCalculator 
{
	public void executeAllVersions (String jarName, int versionNum, String path, String projectName) throws IOException, InterruptedException
	{
		int exitval;

		for (int i = 0; i < versionNum; i++)
		{
			// Check if jar file exists
			File f = new File(path + File.separator + projectName + File.separator + jarName + i + ".jar");
			if(f.exists() && !f.isDirectory()) 
			{ 
				Process metricsAnalysisProcess = Runtime.getRuntime()
						.exec("java -jar externalTools/metrics_calculator.jar " + path + File.separator + projectName + File.separator + jarName + i + ".jar" + " output" + i + ".csv");
				metricsAnalysisProcess.waitFor();
				// If exit value is 0 then execution was successful
				exitval = metricsAnalysisProcess.exitValue();

				if (exitval != 0) 
				{
					System.out.println("An error occured during execution.The project didn't analyzed.");	
				}
				else
				{
					System.out.println("Metrics Calculation Tool executed successfully!");
				}
			}
			else
			{
				System.out.println("Metrics Calculator does not exist. The program will terminate.");
				System.exit(0);
			}
		}
	}

	public void executeOneVersion(String jarName, int version, String path, String projectName) throws IOException, InterruptedException
	{
		int exitval;
		System.out.println("----- Start Metrics Calculator -----");
		
		// Jar file if you execute this tool as jar file, eclipse and server
		File f = new File(path + File.separator + projectName + File.separator + jarName + version + ".jar");
		
		// Run On Docker in Server Command
		//String javaRunningDirectory = System.getProperty("user.dir");
		//File f = new File(javaRunningDirectory + File.separator + projectName + File.separator + jarName + version + ".jar");
		
		// Jar file if you execute this tool from eclipse
		//File f = new File(System.getProperty("user.dir") + "/jars/" +jarName + version + ".jar");
		
		System.out.println(f);
		
		if(f.exists() && !f.isDirectory()) 
		{ 
			// Run On Docker in Server Command
			//String execution = "java -jar metrics_calculator.jar " + path + File.separator + projectName + File.separator + jarName + version + ".jar" + " output" + version + ".csv";
			// if you execute from eclipse
			String execution = "java -jar externalTools/metrics_calculator.jar " + path + File.separator + projectName + File.separator + jarName + version + ".jar" + " output" + version + ".csv";
			System.out.println(execution);
			//Process metricsAnalysisProcess = Runtime.getRuntime()
					//.exec("java -jar externalTools/metrics_calculator.jar " + System.getProperty("user.dir") + "/jars/" +jarName + version + ".jar" + " output" + version + ".csv");
			Process metricsAnalysisProcess = Runtime.getRuntime()
					.exec(execution);
			metricsAnalysisProcess.waitFor();
			// If exit value is 0 then execution was successful
			exitval = metricsAnalysisProcess.exitValue();

			if (exitval != 0) 
			{
				System.out.println("An error occured during execution.The project didn't analyzed.");	
			}
			else
			{
				System.out.println("Metrics Calculation Tool executed successfully!");
			}
			
			metricsAnalysisProcess.destroy();
			if (metricsAnalysisProcess.isAlive()) 
			{
				metricsAnalysisProcess.destroyForcibly();
			}

		}
		else
		{
			System.out.println("Metrics Calculator does not exist. The program will terminate.");
			System.exit(0);
		}
	}

}
