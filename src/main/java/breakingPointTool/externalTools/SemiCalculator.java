package main.java.breakingPointTool.externalTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SemiCalculator 
{
	private final String semi = "externalTools/metrics_calculator_noOop.jar";
	//private final String semi = "metrics_calculator_noOop.jar";


	public void executeSemiCalculator(String language, int version, String path, String projectName, String credentials) throws IOException, InterruptedException
	{
		if (language.equals("C"))
		{
			language = "c";
		}
		else if (language.equals("C++"))
		{
			language = "cpp";
		}
		else if(language.equals("Java"))
		{
			language = "java";
		}
		
		System.out.println("----- Start Semi Calculator -----");
		
		// Jar file if you execute this tool as jar file
		//File f = new File(path + File.separator + projectName + File.separator + jarName + version + ".jar");
		
		// Run On Docker in Server Command
		//String javaRunningDirectory = System.getProperty("user.dir");
		//File f = new File(javaRunningDirectory + File.separator + projectName + File.separator + projectName + version + ".jar");
		
		// Jar file if you execute this tool from eclipse
		File f = new File(path);
		System.out.println("Execution: " + "java -jar " + semi + " " + language + " " + projectName + " " + version + 
					" " + path + " " + credentials);
		
		if(f.exists()) 
		{ 
			// Run On Docker in Server Command
			//String execution = "java -jar " + semi + " " + language + " " + projectName + " " + version + 
					//" " + path + " " + credentials;

			// if you execute from eclipse
			//String execution = "java -jar externalTools/metrics_calculator.jar " + System.getProperty("user.dir") + "/jars/" +jarName + version + ".jar" + " output" + version + ".csv";
			//System.out.println(execution);
			//Process metricsAnalysisProcess = Runtime.getRuntime()
					//.exec("java -jar externalTools/metrics_calculator.jar " + System.getProperty("user.dir") + "/jars/" +jarName + version + ".jar" + " output" + version + ".csv");
			
			ArrayList<String> command = new ArrayList<String>();
			command.add("java");
			command.add("-jar");
			command.add(semi);
			command.add(language);
			command.add(projectName);
			command.add(String.valueOf(version));
			command.add(path);
			command.add(credentials);
			
			ProcessBuilder processB = new ProcessBuilder();
			
			processB.command(command);
			
			try {

				Process process = processB.start();

				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
					//System.out.println(line);
				}

				int exitCode = process.waitFor();
				System.out.println("\nExited with error code : " + exitCode);

				process.destroy();
				if (process.isAlive()) {
					process.destroyForcibly();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Semi Calculator does not exist. The program will terminate.");
			System.exit(0);
		}
	}
	
}
