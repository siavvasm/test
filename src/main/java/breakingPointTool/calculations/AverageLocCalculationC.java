package main.java.breakingPointTool.calculations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.breakingPointTool.api.SonarQubeMetrics;
import main.java.breakingPointTool.artifact.FileMetricsC;
import main.java.breakingPointTool.artifact.PackageMetricsC;
import main.java.breakingPointTool.artifact.ProjectArtifact;
import main.java.breakingPointTool.database.DatabaseGetData;
import main.java.breakingPointTool.database.DatabaseSaveDataC;
import main.java.breakingPointTool.externalTools.SemiCalculator;

public class AverageLocCalculationC 
{
	private ArrayList<FileMetricsC> fileMetrics = new ArrayList<FileMetricsC>();
	private ArrayList<PackageMetricsC> packageMetrics = new ArrayList<PackageMetricsC>();
	
	public void setMetricsToClassLevel(SonarQubeMetrics object, String projectName, int versionNum, String language, String path, String credentials) throws NumberFormatException, IOException, SQLException, InterruptedException
	{
		for (int i = 0; i < object.getArtifactNames().size(); i ++)
		{
			String className = object.getArtifactNames().get(i);
			double classes = object.getNumOfClasses().get(i);
			double lines_of_code = object.getNcloc().get(i);
			double complexity = object.getComplexity().get(i);
			double functions = object.getFunctions().get(i);
			double statements = object.getStatements().get(i);
			double TD = object.getTechnicalDebt().get(i);
			double comment_lines_density = object.getCommentsDensity().get(i);
			double duplications = object.getDuplicationsDensity().get(i);
			
			double bugs = object.getBugs().get(i);
			double codeSmells = object.getCodeSmells().get(i);
			double vulnerabilities = object.getVulnerabilities().get(i);
			String scope = "FIL";
			double principal = 0;

			/*System.out.println("--------------- Class name: " + className + " classes: " + classes + " lines of code: " + lines_of_code +
					" complexity: " + complexity + " functions: " + functions + " statements + " + statements + 
					" TD: " + TD + " comments density: " + comment_lines_density + " code smells: " + codeSmells);*/

			DatabaseSaveDataC saveInDataBase = new DatabaseSaveDataC();
			saveInDataBase.saveMetricsInDatabase(projectName, versionNum, className, scope, lines_of_code, 
					complexity, functions, comment_lines_density, 0, 0 );
			
			saveInDataBase.savePrincipalMetrics(className, projectName, versionNum, TD, principal,
					codeSmells,bugs,vulnerabilities,duplications, scope, classes, complexity, functions, lines_of_code, statements,comment_lines_density, language);
			
			FileMetricsC cm = new FileMetricsC(projectName, className);
			cm.metricsfromSonar(classes, complexity, functions, lines_of_code, statements, TD, comment_lines_density, codeSmells, bugs, vulnerabilities, duplications);
			//cm.metricsfromMetricsCalculator(className, versionNum);
			fileMetrics.add(cm);
		}
		
		// SEMI Execution
		SemiCalculator semi = new SemiCalculator();
		semi.executeSemiCalculator(language, versionNum, path, projectName, credentials);
	}
	
	public void setClassToPackageLevel(ArrayList<String> longNamePackage, String projName, int version, SonarQubeMetrics apiCall, String language) throws NumberFormatException, SQLException, IOException
	{	
		this.packageMetrics = new ArrayList<PackageMetricsC>();
		
		
		for (int i = 0; i < longNamePackage.size(); i++)
		{
			//System.out.println("Package name: " + longNamePackage.get(i));
			String p1 =longNamePackage.get(i);
			//System.out.println("Num of packages api call: " + apiCall.getNumOfClasses().size());
			for (int w = 0; w < apiCall.getNcloc().size(); w++)
			{	
				String p2 = apiCall.getArtifactNames().get(w);
				if (p1.equals(p2))
				{	
					String scope = "DIR";
					double principal = 0;
					
					DatabaseSaveDataC saveInDataBase = new DatabaseSaveDataC();
					
					System.out.println(longNamePackage.get(i) + " " + projName+ " " + version+ " " + apiCall.getTechnicalDebt().get(w)+ " " + principal + " " +
							apiCall.getCodeSmells().get(w) + " " +apiCall.getBugs().get(w) + " " + apiCall.getVulnerabilities().get(w)+ " " +
							apiCall.getDuplicationsDensity().get(w)+ " " + scope+ " " + apiCall.getNumOfClasses().get(w)+ " " + apiCall.getComplexity().get(w) + " " +
							apiCall.getFunctions().get(w)+ " " + apiCall.getNcloc().get(w)+ " " + apiCall.getStatements().get(w) + " " +apiCall.getCommentsDensity().get(w)+ " " + language);

					saveInDataBase.savePrincipalMetrics(p1, projName, version, apiCall.getTechnicalDebt().get(w), principal,
							apiCall.getCodeSmells().get(w),apiCall.getBugs().get(w),apiCall.getVulnerabilities().get(w),
							apiCall.getDuplicationsDensity().get(w), scope, apiCall.getNumOfClasses().get(w), apiCall.getComplexity().get(w), 
							apiCall.getFunctions().get(w), apiCall.getNcloc().get(w), apiCall.getStatements().get(w),apiCall.getCommentsDensity().get(w), language);				
					
					PackageMetricsC p = new PackageMetricsC(projName, p1);					
					p.metricsfromSonar(apiCall.getNumOfClasses().get(w),apiCall.getComplexity().get(w), apiCall.getFunctions().get(w),
							apiCall.getNcloc().get(w), apiCall.getStatements().get(w), apiCall.getTechnicalDebt().get(w), apiCall.getCommentsDensity().get(w),
							apiCall.getCodeSmells().get(w), apiCall.getBugs().get(w), apiCall.getVulnerabilities().get(w), apiCall.getDuplicationsDensity().get(w));
					this.packageMetrics.add(p);
					
					
					break;
				}
			}
		}
		
		for (int i = 0; i < this.packageMetrics.size(); i++)
		{
			String packName = this.packageMetrics.get(i).getPackageName();
			
			for (int j = 0; j < this.fileMetrics.size(); j++)
			{
				int index = this.fileMetrics.get(j).getClassName().lastIndexOf("/");
				String packNameOfClass = this.fileMetrics.get(j).getClassName().substring(0,index);	
				
				if (packName.equals(packNameOfClass))
				{
					this.fileMetrics.get(j).metricsfromMetricsCalculator(this.fileMetrics.get(j).getClassName(), version);
					this.packageMetrics.get(i).setClassInPackage(this.fileMetrics.get(j));
				}
			}

			this.packageMetrics.get(i).calculateMetricsPackageLevel(version);
		}
	}
	
	public ArrayList<String> findAllClasses(ProjectArtifact p)
	{
		ArrayList<String> classes = new ArrayList<String>();

		for (int i = 0; i < p.getVersions().size(); i++)
		{
			for (int j = 0; j < p.getVersions().get(i).getPackagesC().size(); j++)
			{
				for (int z = 0; z < p.getVersions().get(i).getPackagesC().get(j).getClassInProject().size(); z++)
				{
					if (!classes.contains(p.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(z).getClassName()))
					{
						classes.add(p.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(z).getClassName());
					}

				}
			}
		}
		return classes;		
	}

	public ArrayList<String> findAllPackages(ProjectArtifact p)
	{
		ArrayList<String> packages = new ArrayList<String>();

		for (int i = 0; i < p.getVersions().size(); i++)
		{
			for (int j = 0; j < p.getVersions().get(i).getPackagesC().size(); j++)
			{
				if (!packages.contains(p.getVersions().get(i).getPackagesC().get(j).getPackageName()))
				{
					packages.add(p.getVersions().get(i).getPackagesC().get(j).getPackageName());
				}
			}
		}
		return packages;		
	}

	public void calculateLocClassLevel(ProjectArtifact p)
	{
		ArrayList<String> classes = findAllClasses(p);

		for (int cl = 0; cl < classes.size(); cl++)
		{
			String currentClass = classes.get(cl);
			//System.out.println("Current class: " + currentClass);
			ArrayList<Double> sizes = new ArrayList<Double>();

			for (int i = 0; i < p.getVersions().size(); i++)
			{
				//System.out.println("Version: " +  i);
				int flag = 0;
				int packId = 0;
				int classId = 0;
				for (int j = 0; j < p.getVersions().get(i).getPackagesC().size(); j++)
				{				
					for (int z = 0; z < p.getVersions().get(i).getPackagesC().get(j).getClassInProject().size(); z++)
					{					
						if (currentClass.equals(p.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(z).getClassName())) 
						{
							sizes.add(p.getVersions().get(i).getPackagesC().get(j).getClassInProject().get(z).getNcloc());
							flag = 1;
							classId = z;
							break;
						}	
					}
					if (flag == 1)
					{
						packId = j;
						break;		
					}
				}
				double x = 0;
				int aboveZero = 0;

				if (sizes.size() == 1)
				{
					x = 0;
					aboveZero = 1;
				}
				else if (sizes.size() == 2)
				{
					x = Math.abs(sizes.get(1) - sizes.get(0));
					aboveZero = 1;
				}

				for (int l = 1 ; l < sizes.size(); l++)
				{
					double diff = Math.abs(sizes.get(l) - sizes.get(l-1));
					x = x + diff;
					aboveZero++;
				}

				if (!Double.isNaN(x/aboveZero))
				{
					if (currentClass.equals(p.getVersions().get(i).getPackagesC().get(packId).getClassInProject().get(classId).getClassName()))
					{
						p.getVersions().get(i).getPackagesC().get(packId).getClassInProject().get(classId).setAverageInterest(x/aboveZero);
						//System.out.println("For class: " + p.getVersions().get(i).getPackages().get(packId).getClassInProject().get(classId).getClassName());
						//System.out.println("The LOC is: " + x/sizes.size());
						//System.out.println("check loc: " + p.getVersions().get(i).getPackagesC().get(packId).getClassInProject().get(classId).getAverageNocChange());
					}
				}
			}
		}
	}

	public void calculateLocPackageLevel(ProjectArtifact p)
	{		
		ArrayList<String> packages = findAllPackages(p);

		for (int pk = 0; pk < packages.size(); pk++)
		{
			String currentPackage = packages.get(pk);
			//System.out.println("Current package: " + currentPackage);
			ArrayList<Double> sizes = new ArrayList<Double>();

			for (int i = 0; i < p.getVersions().size(); i++)
			{
				//System.out.println("Version: " +  i);
				int packId = 0;
				for (int j = 0; j < p.getVersions().get(i).getPackagesC().size(); j++)
				{							
					if (currentPackage.equals(p.getVersions().get(i).getPackagesC().get(j).getPackageName())) 
					{
						sizes.add(p.getVersions().get(i).getPackagesC().get(j).getNcloc());
						packId = j;
						break;
					}	
				}

				double x = 0;
				int aboveZero = 0;
				
				if (sizes.size() == 1)
				{
					x = 0 ; //sizes.get(0);
					aboveZero = 1;
				}
				else if (sizes.size() == 2)
				{
					x = Math.abs(sizes.get(1) - sizes.get(0));
					aboveZero = 1;
				}

				for (int l = 1 ; l < sizes.size(); l++)
				{
					double diff = Math.abs(sizes.get(l) - sizes.get(l-1));
					x = x + diff;
					//if (diff > 0)
					aboveZero++;
						
				}

				if (!Double.isNaN(x/aboveZero))
				{
					if (currentPackage.equals(p.getVersions().get(i).getPackagesC().get(packId).getPackageName()))
					{
						p.getVersions().get(i).getPackagesC().get(packId).setAverageInterest(x/aboveZero);
						//System.out.println("For package: " + p.getVersions().get(i).getPackages().get(packId).getPackageName());
						//System.out.println("The LOC is: " + x/sizes.size());
						//System.out.println("check loc: " + p.getVersions().get(i).getPackagesC().get(packId).getAverageNocChange());

					}
				}
			}
		}
	}
	
	public void calculateLOCClassLevelNewVersion(HashMap<String, FileMetricsC> previous, ArrayList<PackageMetricsC> currentVersion, int versions )
	{	
		for (int j = 0; j < currentVersion.size(); j++)
		{
			//currentVersion.get(j).getClassInProject(); 
			// for each package
			
			for (int i = 0; i < currentVersion.get(j).getClassInProject().size(); i++)
			{
				// for each class
				FileMetricsC current = currentVersion.get(j).getClassInProject().get(i);

				if (previous.containsKey(currentVersion.get(j).getClassInProject().get(i).getClassName()))
				{
					
					ArrayList<Double> locs = new ArrayList<Double>();
			    	// Get k values from database
			    	DatabaseGetData dbCall = new DatabaseGetData();
			    	locs.addAll(dbCall.getLoCForArtifactC(currentVersion.get(j).getClassInProject().get(i).getClassName(), versions));
			    	
			    	// real number of versions
			    	int v = locs.size();
			    	
			    	if (v == 0)
			    		break;
					
					FileMetricsC previousClass = previous.get(currentVersion.get(j).getClassInProject().get(i).getClassName());
					
					//System.out.println("Old average loc before multis: " + previousClass.getAverageNocChange());
					double oldLoc = Math.round(previousClass.getAverageNocChange() * (v-2));
					//System.out.println("Old average loc: " + oldLoc);
					//System.out.println("Current LOC: " + current.getNcloc());
					//System.out.println("Previous LOC: " + previousClass.getNcloc());
					double currentLoc = Math.abs(current.getNcloc() - previousClass.getNcloc());

					double loc = (oldLoc + currentLoc) / (v-1); 
					//System.out.println("Before and after: " + oldLoc + " " + loc);
					//System.out.println("For class: " + current.getClassName() + 
							//" the loc is: " + loc);
					currentVersion.get(j).getClassInProject().get(i).setAverageInterest(loc);
				}
		
			}
		}
	}
	
	public void calculateLOCPackageLevelNewVersion(HashMap<String, PackageMetricsC> previous, ArrayList<PackageMetricsC> currentVersion, int versions )
	{	
		for (int i = 0; i < currentVersion.size(); i++)
		{
			//System.out.println("---------- Package: " + currentVersion.get(i).getPackageName());
			if (previous.containsKey(currentVersion.get(i).getPackageName()))
			{
				
				ArrayList<Double> locs = new ArrayList<Double>();
		    	// Get k values from database
		    	DatabaseGetData dbCall = new DatabaseGetData();
		    	locs.addAll(dbCall.getLoCForArtifactC(currentVersion.get(i).getPackageName(), versions));
		    	
		    	// real number of versions
		    	int v = locs.size();
		    	
		    	if (v == 0)
		    		break;
		    	
				PackageMetricsC c = previous.get(currentVersion.get(i).getPackageName());
				double oldLoc = Math.round(c.getAverageNocChange() * (v - 2));
				
				double currentLoc = Math.abs(currentVersion.get(i).getNcloc() - c.getNcloc());
				
				double loc = (oldLoc + currentLoc) / (v-1);  
				//System.out.println("Before and after: " + oldLoc + " " + loc);
				
				//System.out.println("For package: " + currentVersion.get(i).getPackageName() + 
						//" the loc is: " + loc);
				currentVersion.get(i).setAverageInterest(loc);
			}
		}
		
	}
	
	public ArrayList<FileMetricsC> getObjectsClassMetrics()
	{
		return this.fileMetrics;
	}

	public ArrayList<PackageMetricsC> getObjectsPackageMetrics()
	{
		return this.packageMetrics;
	}

}
