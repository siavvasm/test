package main.java.breakingPointTool.artifact;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import main.java.breakingPointTool.database.DatabaseSaveData;

//This class contains metrics for Java Language
public class PackageMetrics 
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

	// Metrics Calculator Tool Metrics
	private double MPC;	
	private double DIT;
	private double NOCC;
	private double RFC;
	private double LCOM;
	private double WMPC;
	private double DAC;
	private double NOM;
	private double SIZE1;
	private double SIZE2;
	
	// Ripple Effect and Change Proneness Measures
	private double rem;
	private double mcpm;
	
	// Principal Metrics
	private double bugs;
	private double codeSmells;
	private double vulnerabilities;
	private double duplicated_lines_density;

	// Breaking Point Tool Metrics
	private ArrayList<Double> locChange;
	private double averageLocChange;
	private ArrayList<ClassMetrics> classInPackage;
	private double interest_probability;

	public PackageMetrics(String projectName, String packageName)
	{
		this.locChange = new ArrayList<Double>();
		this.classInPackage = new ArrayList<ClassMetrics>();
		this.packageName = packageName;	
		this.projectName =projectName;
		this.classes = 0;
		this.complexity = 0;
		this.functions = 0;
		this.nloc = 0;
		this.statements = 0;		
		this.TD = 0;

		this.MPC = 0;
		this.DIT = 0;
		this.NOCC = 0;
		this.RFC = 0;
		this.LCOM = 0;
		this.WMPC = 0; 
		this.DAC = 0;
		this.NOM = 0;
		this.SIZE1 = 0;
		this.SIZE2 = 0;
		
		this.rem = 0;
		this.mcpm = 0;
		
		this.bugs = 0;
		this.codeSmells = 0;
		this.vulnerabilities = 0;
		this.duplicated_lines_density = 0;
		
		this.interest_probability = 0;
		this.averageLocChange = 0;
	}

	public void metricsfromSonar(double classes, double complexity, double functions,double nlock, double statements, double TD, double codeSmells, double bugs, double vulnerabilities, double duplicated_lines_density) 
	{
		this.classes = classes;
		this.complexity = complexity;
		this.functions = functions;
		this.nloc = nlock;
		this.statements = statements;
		this.TD = TD;
		
		this.bugs = bugs;
		this.codeSmells = codeSmells;
		this.vulnerabilities = vulnerabilities;
		this.duplicated_lines_density = duplicated_lines_density;
	}
	
	public void metricsfromMetricsCalculator(double MPC, double NOM, double DIT, double NOCC, double RFC, double LCOM, double WMPC, double DAC, double SIZE1, double SIZE2)
	{
		this.MPC =  MPC; 
		this.DIT = DIT;
		this.NOCC = NOCC;
		this.RFC = RFC;
		this.LCOM = LCOM;
		this.WMPC = WMPC;
		this.DAC = DAC;
		this.NOM = NOM;
		this.SIZE1 = SIZE1;
		this.SIZE2 = SIZE2;
	}
	
	public void setInterestProbability(double ip)
	{
		this.interest_probability = ip;
	}
	
	public double getInterestProbability()
	{
		return this.interest_probability;
	}
	
	public void metricsfromChangeProneness(double rem, double mcpm) 
	{
		this.rem = rem;
		this.mcpm = mcpm;
	}
	
	public void setDuplicationsDensity(double duplications)
	{
		this.duplicated_lines_density = duplications;
	}
	
	public void setBugs(double bugs)
	{
		this.bugs = bugs;
	}
	
	public void setCodeSmells(double codeSmells)
	{
		this.codeSmells = codeSmells;
	}
	
	public void setVulnerabilities(double vulnerabilities)
	{
		this.vulnerabilities = vulnerabilities;
	}

	public void calculateMetricsPackageLevel(int version) throws NumberFormatException, SQLException, IOException
	{
		int num = classInPackage.size();
		double mpc = 0, nocc = 0, rfc = 0, lcom = 0, wmpc = 0, dac = 0;	
		double nom = 0, size1 = 0, size2 = 0;
		double value = 0;
		for  (int i = 0; i < classInPackage.size(); i++)
		{
			nom = nom + classInPackage.get(i).getNom();
			size1 = size1 + classInPackage.get(i).getSize1();
			size2 = size2 + classInPackage.get(i).getSize2();
			
			mpc = mpc + classInPackage.get(i).getMpc();
			nocc = nocc + classInPackage.get(i).getNocc();
			rfc= rfc + classInPackage.get(i).getRfc();
			lcom = lcom + classInPackage.get(i).getLcom();
			wmpc = wmpc + classInPackage.get(i).getWmpc();
			dac = dac + classInPackage.get(i).getDac();
			
			if (classInPackage.get(i).getDit() > value)
			{
				value = classInPackage.get(i).getDit();
			}
		}
		
		this.NOM = nom;
		this.SIZE1 = size1;
		this.SIZE2 = size2;
		this.DIT = value;
		
		this.MPC = mpc / num;
		this.NOCC = nocc /num;
		this.RFC = rfc / num;
		this.LCOM = lcom / num;
		this.WMPC = wmpc / num;
		this.DAC = dac / num;
		String scope =  "DIR";

		DatabaseSaveData saveInDataBase = new DatabaseSaveData(); 
		//System.out.println("Project name: " + projectName + " version: "  + version + " packageName: " + this.getPackageName() + " scope: " +  scope
			//	+ " NOM: " +  this.NOM + " DIT: " + this.DIT + " RFC: " +  this.RFC + " LCOM: " +  this.LCOM + " WMPC: " + this.WMPC + 
			//	" NOCC: " + this.NOCC + " MPC: " +  this.MPC + " DAC: " + this.DAC + " SIZE1: " + this.SIZE1 + " SIZE2: " + this.SIZE2);		
		
		if (Double.isNaN(this.RFC)) 
		{
			this.RFC = 0;
			this.NOCC = 0;
			this.LCOM = 0;
			this.WMPC = 0;
			this.MPC = 0;
			this.DAC = 0;			
		}
		
		saveInDataBase.saveMetricsInDatabase(projectName, version, this.getPackageName(), scope, this.NOM, this.DIT, -1.0, this.RFC, this.LCOM, this.WMPC, this.NOCC, this.MPC, this.DAC, this.SIZE1, this.SIZE2,
				-1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0,this.NOM, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, this.rem, this.mcpm);
	}

	public void addSizeInArraylist(double s)
	{
		this.locChange.add(s);
	}	

	public void setSize1(double size1)
	{
		this.SIZE1 = size1;
	}
	public void setAverageLocChange(double av)
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
	
	public void setClassInPackage(ClassMetrics cm)
	{
		this.classInPackage.add(cm);
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
	
	public ArrayList<ClassMetrics> getClassInProject()
	{
		return this.classInPackage;
	}

	public double getSize1()
	{
		return this.SIZE1;
	}

	public double getAverageLocChange()
	{
		return this.averageLocChange;
	}

	public double getNocc()
	{
		return this.NOCC;
	}

	public double getTD()
	{
		return this.TD;
	}

	public double getNumOfClasses()
	{
		return this.classes;
	}

	public double getComplexity()
	{
		return this.complexity;
	}

	public double getFunctions()
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

	public double getDit()
	{
		return this.DIT;
	}

	public double getMpc()
	{
		return this.MPC;
	}

	public double getRfc()
	{
		return this.RFC;
	}

	public double getLcom()
	{
		return this.LCOM;
	}

	public double getDac()
	{
		return this.DAC;
	}

	public double getNom()
	{
		return this.NOM;
	}

	public double getWmpc()
	{
		return this.WMPC;
	}

	public double getSize2()
	{
		return this.SIZE2;
	}
	
	public double getRem()
	{
		return this.rem;
	}

	public double getMcpm()
	{
		return this.mcpm;
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

	public void print()
	{
		System.out.println(packageName + "; " + classes + "; " + complexity + "; " + nloc + "; " + functions + "; " + statements + "; " + TD + "; " + MPC + "; " + DIT + "; " + NOCC + "; " + RFC + "; " + LCOM + "; " + WMPC + "; " + DAC + "; " + NOM + ";" + SIZE1 + "; " + SIZE2);

	}

}

