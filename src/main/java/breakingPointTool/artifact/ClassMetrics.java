package main.java.breakingPointTool.artifact;

// This class contains metrics for Java Language
public class ClassMetrics 
{
	private String projectName;
	private String className;
	
	// SonarQube Metrics Java
	private double classes;
	private double complexity;
	private double functions;
	private double nloc;
	private double statements;	
	private double TD;
	
	// Metrics Calculator Tool Metrics Java
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
	private double cpm;
	
	// Principal Metrics
	private double bugs;
	private double codeSmells;
	private double vulnerabilities;
	private double duplicated_lines_density;
	
	// Breaking Point Tool Metrics
	private double averageLocChange;
	private double interest_probability;
	
	public ClassMetrics(String projectName, String className)
	{
		this.className = className;	
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
		this.cpm = 0;
		
		this.bugs = 0;
		this.codeSmells = 0;
		this.vulnerabilities = 0;
		this.duplicated_lines_density = 0;
		
		this.interest_probability = 0;
		this.averageLocChange = 0;
	}
		
	public void metricsfromSonar(double classes, double complexity, double functions,double nloc, double statements, double TD, double codeSmells, double bugs, double vulnerabilities, double duplicated_lines_density) 
	{
		this.classes = classes;
		this.complexity = complexity;
		this.functions = functions;
		this.nloc = nloc;
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
	
	public void metricsfromChangeProneness(double rem, double cpm) 
	{
		this.rem = rem;
		this.cpm =cpm;
	}
	
	public void setSize1(double size1)
	{
		this.SIZE1 = size1;
	}
	public void setAverageLOCChange(double av)
	{
		this.averageLocChange = av;
	}
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public void setProjectName(String projName)
	{
		this.projectName = projName;
	}
	
	public String getProjectName()
	{
		return this.projectName;
	}
	
	public String getClassName()
	{
		return this.className;
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

	public double getCpm()
	{
		return this.cpm;
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
		System.out.println(className + "; " + classes + "; " + complexity + "; " + nloc + "; " + functions + "; " + statements + "; " + TD + "; " + MPC + "; " + DIT + "; " + NOCC + "; " + RFC + "; " + LCOM + "; " + WMPC + "; " + DAC + "; " + NOM + ";" + SIZE1 + "; " + SIZE2);

	}

}
