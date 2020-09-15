package main.java.breakingPointTool.artifact;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import main.java.breakingPointTool.database.DatabaseSaveDataC;

public class PackageMetricsC 
{
	private String projectName;
	private String packageName;
	
	// SonarQube Metrics
	private double classes;
	private double complexity;
	private double functions;
	private double nloc;
	private double statements;	
	private double TD;
	private double comment_lines_density;
	
	// CCCC Tool Metrics
	private double lines_of_code ;	
	private double McCabes_cyclomatic_complexity;
	private double weighted_methods_per_class_unity;
	private double lines_of_code_per_line_of_comment;
	
	// Principal Metrics
	private double bugs;
	private double codeSmells;
	private double vulnerabilities;
	private double duplicated_lines_density;
	
	//Couling and Cohesion
	private double coupling;
	private double cohesion;
	
	// Breaking Point Tool Metrics
	private ArrayList<Double> locChange;
	private double averageLocChange;
	private ArrayList<FileMetricsC> filesInPackage;
	
	public PackageMetricsC (String projectName, String packageName)
	{
		this.locChange = new ArrayList<Double>();
		this.filesInPackage = new ArrayList<FileMetricsC>();
		this.packageName = packageName;	
		this.projectName =projectName;
		this.classes = 0;
		this.complexity = 0;
		this.functions = 0;
		this.nloc = 0;
		this.statements = 0;		
		this.TD = 0;
		this.comment_lines_density = 0;
		
		this.lines_of_code = 0;
		this.McCabes_cyclomatic_complexity = 0;
		this.weighted_methods_per_class_unity = 0;
		this.lines_of_code_per_line_of_comment = 0;
		
		this.bugs = 0;
		this.codeSmells = 0;
		this.vulnerabilities = 0;
		this.duplicated_lines_density = 0;
		
		this.averageLocChange = 0;
		
		this.coupling = 0;
		this.cohesion = 0;
	}
	
	
	public void metricsfromSonar(double classes, double complexity, double functions,double nlock, double statements, double TD, double comment_lines_density, double codeSmells, double bugs, double vulnerabilities, double duplicated_lines_density) 
	{
		this.classes = classes;
		this.complexity = complexity;
		this.functions = functions;
		this.nloc = nlock;
		this.statements = statements;
		this.TD = TD;
		this.comment_lines_density = comment_lines_density;
		
		this.bugs = bugs;
		this.codeSmells = codeSmells;
		this.vulnerabilities = vulnerabilities;
		this.duplicated_lines_density = duplicated_lines_density;
	}
	
	public void setBugs(double bugs)
	{
		this.bugs = bugs;
	}
	
	public void setDuplicationsDensity(double duplications)
	{
		this.duplicated_lines_density = duplications;
	}
	
	public void setCodeSmells(double codeSmells)
	{
		this.codeSmells = codeSmells;
	}
	
	public void setVulnerabilities(double vulnerabilities)
	{
		this.vulnerabilities = vulnerabilities;
	}
	
	public void setCoupling(double c) 
	{
		this.coupling = c;
	}
	
	public void setCohesion(double c) 
	{
		this.cohesion = c;
	}
	
	public void calculateMetricsPackageLevel(int version) throws NumberFormatException, SQLException, IOException
	{
		int num = this.filesInPackage.size();
		double lines_of_codeTemp = 0, complexityTemp = 0, functionsTemp = 0;
		double comment_lines_density = 0;
		double couplingTemp = 0, cohesionTemp = 0;
		for  (int i = 0; i < this.filesInPackage.size(); i++)
		{
			lines_of_codeTemp = lines_of_codeTemp + this.filesInPackage.get(i).getNcloc();
			complexityTemp = complexityTemp + this.filesInPackage.get(i).getComplexity();
			functionsTemp = functionsTemp + this.filesInPackage.get(i).getNumOfFunctions();
			comment_lines_density = comment_lines_density + this.filesInPackage.get(i).getCommentsDensity();
			couplingTemp = couplingTemp + this.filesInPackage.get(i).getCoupling();
			cohesionTemp = cohesionTemp + this.filesInPackage.get(i).getCohesion();
			
			/*
			 * coupling_between_objects = coupling_between_objects +
			 * classInPackage.get(i).getCoumpligBetweenObjects();
			 * 
			 * if (classInPackage.get(i).getDepthOfInheritanceTree() > value) { value =
			 * classInPackage.get(i).getDepthOfInheritanceTree(); }
			 * 
			 * if (classInPackage.get(i).getNumberOfChildren() > maxChildren) { maxChildren
			 * = classInPackage.get(i).getNumberOfChildren(); }
			 */
		}
		this.nloc = lines_of_codeTemp;
		this.comment_lines_density = comment_lines_density/num;
		this.complexity = complexityTemp/num;
		this.functions = functionsTemp/num;
		this.coupling = couplingTemp / num;
		this.cohesion = cohesionTemp / num;
		String scope =  "DIR";

		//DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
		/*System.out.println("Project name: " + projectName + " version: "  + version + " packageName: " + this.getPackageName() + " scope: " +  scope
				+ " lines_of_code: " +  this.nloc + " McCabes_cyclomatic_complexity: " + this.complexity + " weighted_methods_per_class_unity: " +  this.functions + 
				" commends density: " +  this.comment_lines_density);	*/	
		
		if (Double.isNaN(this.McCabes_cyclomatic_complexity)) 
		{
			this.McCabes_cyclomatic_complexity = 0;		
		}
		
		DatabaseSaveDataC saveInDataBase = new DatabaseSaveDataC();
		
		saveInDataBase.saveMetricsInDatabase(projectName, version, this.getPackageName(), scope, this.nloc, 
				this.complexity, this.functions, this.comment_lines_density, this.coupling, this.cohesion);
	}

	public double getCommentsDensity()
	{
		 return this.comment_lines_density;
	}
	
	public double getTD()
	{
		return this.TD;
	}
	
	public double getNumOfFunctions()
	{
		return this.functions;
	}
	
	public double getNcloc()
	{
		return this.nloc;
	}
	
	public double getStatements()
	{
		return this.statements;
	}
	
	public double getNumOfClasses()
	{
		return this.classes;
	}
	
	public double getComplexity()
	{
		return this.complexity;
	}

	
	public void setLinesOfCodePerPineOfComment(double lines_of_code_per_line_of_comment)
	{
		this.lines_of_code_per_line_of_comment = lines_of_code_per_line_of_comment;
	}
	
	public double getLinesOfCodePerPineOfComment()
	{
		return this.lines_of_code_per_line_of_comment;
	}
	
	public void setWeightedMethodsPerClassUnity(double weighted_methods_per_class_unity) 
	{
		this.weighted_methods_per_class_unity = weighted_methods_per_class_unity;
	}
	
	public double getWeightedMethodsPerClassUnity()
	{
		return this.weighted_methods_per_class_unity;
	}
	
	public void setLinesOfCode(double lines_of_code)
	{
		this.lines_of_code = lines_of_code;
	}
	
	public double getLinesOfCode()
	{
		return this.lines_of_code;
	}
	
	public void setMcCabesCyclomaticComplexity(double McCabes_cyclomatic_complexity)
	{
		this.McCabes_cyclomatic_complexity = McCabes_cyclomatic_complexity;
	}
	
	public double getMcCabesCyclomaticComplexity()
	{
		return this.McCabes_cyclomatic_complexity ;
	}
	
	public void addSizeInArraylist(double s)
	{
		this.locChange.add(s);
	}
	
	public void setAverageInterest(double av)
	{
		this.averageLocChange = av;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public void setProjectName(String projName)
	{
		this.projectName = projName;
	}
	
	public void setClassInPackage(FileMetricsC cm)
	{
		this.filesInPackage.add(cm);
	}

	public String getProjectName()
	{
		return this.projectName;
	}

	public String getPackageName()
	{
		return this.packageName;
	}

	public ArrayList<Double> getArraySize1()
	{
		return this.locChange;
	}
	
	public ArrayList<FileMetricsC> getClassInProject()
	{
		return this.filesInPackage;
	}
	
	public double getAverageNocChange() 
	{
		return this.averageLocChange;
	}
	
	public double getBugs()
	{
		return this.bugs;
	}
	
	public double getCodeSmells()
	{
		return this.codeSmells;
	}
	
	public double getVulnerabilities()
	{
		return this.vulnerabilities;
	}
	
	public double getDuplications()
	{
		return this.duplicated_lines_density;
	}
	
	public double getCoupling()
	{
		return this.coupling;
	}
	
	public double getCohesion() 
	{
		return this.cohesion;
	}

}
