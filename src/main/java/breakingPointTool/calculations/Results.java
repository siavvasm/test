package main.java.breakingPointTool.calculations;

import java.sql.SQLException;
import java.util.ArrayList;

import main.java.breakingPointTool.artifact.ClassMetrics;
import main.java.breakingPointTool.artifact.PackageMetrics;
import main.java.breakingPointTool.database.DatabaseGetData;
import main.java.breakingPointTool.database.DatabaseSaveData;

public class Results 
{
	private static final double MINUTES_IN_HOUR = 60.0;
    private static final double HOURLY_LINES_OF_CODE = 25.0;
    private static final double HOURLY_WAGE = 45.81;
    
	private double interest;
	private double principal;
	private double breakingPoint;
	
	private double fitnessValueDit;
	private double fitnessValueNocc;
	private double fitnessValueMpc;
	private double fitnessValueRfc;
	private double fitnessValueLcom;
	private double fitnessValueDac;
	private double fitnessValueNom;
	private double fitnessValueWmpc;
	private double fitnessValueSize1;
	private double fitnessValueSize2;

	
	public Results()
	{
		this.interest = 0;
		this.principal = 0;
		this.breakingPoint = 0;
	}
	
    public void calculateInterest(ClassMetrics investigatedClass, OptimalArtifact optimalClass, int version) throws SQLException
    {
    	double k = investigatedClass.getAverageLocChange();

    	this.fitnessValueDit = calculateFitnessValueMin(optimalClass.getDit(), investigatedClass.getDit());
    	this.fitnessValueNocc = calculateFitnessValueMin(optimalClass.getNocc(), investigatedClass.getNocc());
    	this.fitnessValueMpc = calculateFitnessValueMin(optimalClass.getMpc(), investigatedClass.getMpc());
    	this.fitnessValueRfc = calculateFitnessValueMin(optimalClass.getRfc(), investigatedClass.getRfc());
    	this.fitnessValueLcom = calculateFitnessValueMin(optimalClass.getLcom(), investigatedClass.getLcom());
    	this.fitnessValueDac = calculateFitnessValueMin(optimalClass.getDac(), investigatedClass.getDac());
    	this.fitnessValueNom = calculateFitnessValueMin(optimalClass.getNom(), investigatedClass.getNom());
    	this.fitnessValueWmpc = calculateFitnessValueMin(optimalClass.getWmpc(), investigatedClass.getWmpc());
    	this.fitnessValueSize1 = calculateFitnessValueMin(optimalClass.getSize1(), investigatedClass.getSize1());
    	this.fitnessValueSize2 = calculateFitnessValueMin(optimalClass.getSize2(), investigatedClass.getSize2());
    	
    	this.interest = this.fitnessValueDit + this.fitnessValueNocc + this.fitnessValueMpc + this.fitnessValueRfc + this.fitnessValueLcom + 
    			this.fitnessValueDac + this.fitnessValueNom + this.fitnessValueWmpc + this.fitnessValueSize1 + this.fitnessValueSize2;
    	//System.out.println("gchsgd: " + this.interest);
    	System.out.println("k: " + k);
    	/*System.out.println("Interest test: " + this.fitnessValueDit + " " + this.fitnessValueNocc +" "+ this.fitnessValueMpc +" "+ this.fitnessValueRfc + " "+this.fitnessValueLcom + " "+
    			this.fitnessValueDac +" "+ this.fitnessValueNom +" "+ this.fitnessValueWmpc +" "+ this.fitnessValueSize1 +" "+ this.fitnessValueSize2);*/
    	this.interest = this.interest / 10;
    	this.interest = 1  - this.interest ;
    	System.out.println("Interest is: " + interest);
    	this.interest = this.interest * k;  	
    	this.interest = (this.interest / HOURLY_LINES_OF_CODE) * HOURLY_WAGE;
    	System.out.println("Interest is in money: " + interest);
    	System.out.println("Version: " + version);
    	
    	
    	calculatePrincipal(investigatedClass);
    	calculateBreakingPoint();
    	
    	if (Double.isInfinite(this.breakingPoint))
    	{
    		// It means infinity
    		this.breakingPoint = -1;
    	}
    	else if (Double.isNaN(breakingPoint))
    	{
    		// It means it is not defined
    		this.breakingPoint = -2;
    	}
    	
    	double rate = calculateInterestProbability(investigatedClass.getClassName(), version);
    	System.out.println("Interest probability: " + rate);
    	DatabaseSaveData saveDataInDatabase = new DatabaseSaveData();
    	saveDataInDatabase.saveBreakingPointInDatabase(investigatedClass.getClassName(), version, this.breakingPoint, this.principal, this.interest, k, rate, investigatedClass.getProjectName());

    	saveDataInDatabase.updatePrincipal(investigatedClass.getClassName(), version, this.principal, investigatedClass.getProjectName());
    }
    
    public void calculateInterestPackage(PackageMetrics investigatedPackage, OptimalArtifact optimalClass, int version) throws SQLException
    {
    	double k = investigatedPackage.getAverageLocChange();

    	this.fitnessValueDit = calculateFitnessValueMax(optimalClass.getDit(), investigatedPackage.getDit());
    	this.fitnessValueNocc = calculateFitnessValueMax(optimalClass.getNocc(), investigatedPackage.getNocc());
    	this.fitnessValueMpc = calculateFitnessValueMin(optimalClass.getMpc(), investigatedPackage.getMpc());
    	this.fitnessValueRfc = calculateFitnessValueMin(optimalClass.getRfc(), investigatedPackage.getRfc());
    	this.fitnessValueLcom = calculateFitnessValueMin(optimalClass.getLcom(), investigatedPackage.getLcom());
    	this.fitnessValueDac = calculateFitnessValueMin(optimalClass.getDac(), investigatedPackage.getDac());
    	this.fitnessValueNom = calculateFitnessValueMin(optimalClass.getNom(), investigatedPackage.getNom());
    	this.fitnessValueWmpc = calculateFitnessValueMin(optimalClass.getWmpc(), investigatedPackage.getWmpc());
    	this.fitnessValueSize1 = calculateFitnessValueMin(optimalClass.getSize1(), investigatedPackage.getSize1());
    	this.fitnessValueSize2 = calculateFitnessValueMin(optimalClass.getSize2(), investigatedPackage.getSize2());
    	
    	this.interest = this.fitnessValueDit + this.fitnessValueNocc + this.fitnessValueMpc + this.fitnessValueRfc + this.fitnessValueLcom + 
    			this.fitnessValueDac + this.fitnessValueNom + this.fitnessValueWmpc + this.fitnessValueSize1 + this.fitnessValueSize2;
    	System.out.println("k: " + k);
    	//System.out.println("Interest test: " + this.fitnessValueDit + " " + this.fitnessValueNocc +" "+ this.fitnessValueMpc +" "+ this.fitnessValueRfc + " "+this.fitnessValueLcom + " "+
    		//	this.fitnessValueDac +" "+ this.fitnessValueWmpc +" "+ this.fitnessValueCc +" "+ this.fitnessValueSize1 +" "+ this.fitnessValueSize2);
    	this.interest = this.interest / 10;
    	this.interest = 1  - this.interest ;
    	System.out.println("Interest is: " + interest);
    	this.interest = this.interest * k;  	
    	this.interest = (this.interest / HOURLY_LINES_OF_CODE) * HOURLY_WAGE;
    	System.out.println("Interest is in money: " + interest);
    	
    	calculatePrincipalPackage(investigatedPackage);
    	calculateBreakingPoint();
    	
    	if (Double.isInfinite(this.breakingPoint))
    	{
    		// It means infinity
    		this.breakingPoint = -1;
    	}
    	else if (Double.isNaN(breakingPoint))
    	{
    		// It means it is not defined
    		this.breakingPoint = -2;
    	}
    	
    	double rate = calculateInterestProbability(investigatedPackage.getPackageName(), version);
    	System.out.println("Interest probability: " + rate);
    	DatabaseSaveData saveDataInDatabase = new DatabaseSaveData();
    	saveDataInDatabase.saveBreakingPointInDatabase(investigatedPackage.getPackageName(), version, this.breakingPoint, this.principal, this.interest, k, rate, investigatedPackage.getProjectName()
);
    	
    	saveDataInDatabase.updatePrincipal(investigatedPackage.getPackageName(), version, this.principal, investigatedPackage.getProjectName());

    }
    
    public double calculateInterestProbability(String artifact, int version)
    {
    	ArrayList<Double> locs = new ArrayList<Double>();
    	double flag = 0.0;
    	// Get k values from database
    	DatabaseGetData dbCall = new DatabaseGetData();
    	locs.addAll(dbCall.getLoCForArtifact(artifact, version));
    	
    	// bug when version are less than full
    	
    	if (locs.size() == 0 || locs.size() == 1)
    		return 0.0;
    	
    	if (locs.size() == 2)
    	{
    		double diff = locs.get(1)  - locs.get(0);
    		
    		if (diff != 0)
    			return 1.0;  		
    		else
    			return 0.0;
    	}
    	
    	for (int i = 0; i < locs.size()-1; i++ )
    	{
    		double diff = locs.get(i + 1) - locs.get(i);
    		
    		if (diff != 0)
    		{
    			flag++;
    		}
    	}
 	
    	return flag / (locs.size()-1);
	
    }
    
    public void calculateInterestOnePackage(PackageMetrics investigatedPackage, String projectName, int version) throws SQLException
	{
		ArrayList<Double> interests = new ArrayList<Double>();

		DatabaseGetData dbCall = new DatabaseGetData();
		interests.addAll(dbCall.getInterestForArtifactC(projectName, version));
		
		for (int i = 0; i < interests.size()-1; i++ )
		{
			this.interest =  this.interest +  interests.get(i);
		}
		
		double k = investigatedPackage.getAverageLocChange();

		System.out.println("----- Only one package -----");
		System.out.println("Package Name: " + investigatedPackage.getPackageName());
		System.out.println("Interest is: " + interest);
		System.out.println("K: " + k);
		
		calculatePrincipalPackage(investigatedPackage);
		calculateBreakingPoint();

		if (Double.isInfinite(this.breakingPoint))
		{
			// It means infinity
			this.breakingPoint = -1;
		}
		else if (Double.isNaN(breakingPoint))
		{
			// It means it is not defined
			this.breakingPoint = -2;
		}

		double rate = calculateInterestProbability(investigatedPackage.getPackageName(), version);
		
		DatabaseSaveData saveDataInDatabase = new DatabaseSaveData();
		System.out.println("Before saved in database: " + investigatedPackage.getPackageName() + " " + version + " " +
				this.breakingPoint + " " + this.principal + " " + this.interest + " " + k + " " + rate);
		saveDataInDatabase.saveBreakingPointInDatabase(investigatedPackage.getPackageName(), version, this.breakingPoint, this.principal, this.interest, k, rate, investigatedPackage.getProjectName());
		saveDataInDatabase.updatePrincipal(investigatedPackage.getPackageName(), version, this.principal, investigatedPackage.getProjectName());
		
		
	}
    
    public void calculatePrincipalPackage(PackageMetrics testedClass)
    {
    	this.principal = (testedClass.getTD() / MINUTES_IN_HOUR ) * HOURLY_WAGE;
    	System.out.println("Principal is: " + principal);
    }
    
    public void calculatePrincipal(ClassMetrics testedClass)
    {
    	this.principal = (testedClass.getTD() / MINUTES_IN_HOUR ) * HOURLY_WAGE;
    	System.out.println("Principal is: " + principal);
    }
    
    public void calculateBreakingPoint()
    {
    	//TODO maybe i should change NaN to a number, lets say -1
    	this.breakingPoint = this.principal / this.interest;
    	System.out.println("Breaking point is: " + breakingPoint + "\n");
    }
    
    public double  calculateFitnessValueMin (double optimal, double actual)
    {
    	if (optimal == 00)
    		return 0;
    	else 
    		return optimal / actual;
    }
    
    public double  calculateFitnessValueMax (double optimal, double actual)
    {
    	if (optimal == 00)
    		return 0;
    	else 
    		return actual / optimal;
    }
    
    public double getPrincipal()
    {
    	return this.principal;
    }
    
    public double getInterest()
    {
    	return this.interest;
    }
    
    public double getBreakingPoint()
    {
    	return this.breakingPoint;
    }

}
